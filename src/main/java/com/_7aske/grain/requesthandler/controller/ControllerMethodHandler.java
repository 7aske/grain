package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.constants.ValueConstants;
import com._7aske.grain.http.HttpContentType;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.form.FormBody;
import com._7aske.grain.http.form.FormDataMapper;
import com._7aske.grain.http.json.*;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.requesthandler.controller.wrapper.ControllerMethodWrapper;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.util.HttpPathUtil;
import com._7aske.grain.util.RequestParams;
import com._7aske.grain.web.controller.annotation.PathVariable;
import com._7aske.grain.web.controller.annotation.RequestParam;
import com._7aske.grain.web.controller.converter.Converter;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.view.View;
import com._7aske.grain.web.view.ViewResolver;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

/**
 * This {@link RequestHandler} implementation is a representation of a controller method.
 */
public class ControllerMethodHandler implements RequestHandler {
	/**
	 * Prefix that signalizes that the response is actually a redirect
	 * rather than a response with string body.
	 */
	private static final String REDIRECT_PREFIX = "redirect:";
	private final ControllerMethodWrapper method;
	private final ConverterRegistry converterRegistry;
	private final ViewResolver viewResolver;

	public ControllerMethodHandler(ControllerMethodWrapper method,
	                               ConverterRegistry converterRegistry,
	                               ViewResolver viewResolver) {
		this.method = method;
		this.converterRegistry = converterRegistry;
		this.viewResolver = viewResolver;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {

		// Here we handle Controller method parameter parsing
		Parameter[] declaredParams = method.getParameters();
		Object[] params = new Object[declaredParams.length];
		for (int i = 0; i < declaredParams.length; i++) {
			Parameter param = declaredParams[i];
			if (param.getType().equals(HttpRequest.class)) {
				params[i] = request;
			} else if (param.getType().equals(HttpResponse.class)) {
				params[i] = response;
			} else if (param.getType().equals(Session.class)) {
				params[i] = request.getSession();
			} else if (param.isAnnotationPresent(JsonBody.class)) {
				params[i] = new JsonDeserializer<>(param.getType()).deserialize((JsonObject) request.getBody());
			} else if (param.isAnnotationPresent(FormBody.class)) {
				// Mapping request params from HttpRequest.parameters to either
				// a Map<String, String> or a class specified by the method parameter.
				// This case is different from other because we have to extract
				// the data using request.getParameters instead of request.getBody.
				if (Map.class.isAssignableFrom(param.getType())) {
					params[i] = request.getParameters();
				} else if (RequestParams.class.isAssignableFrom(param.getType())) {
					params[i] = new RequestParams(request.getParameters());
				} else {
					params[i] = new FormDataMapper<>(param.getType()).parse(request.getParameters());
				}
			} else if (param.isAnnotationPresent(RequestParam.class)) {
				RequestParam requestParam = param.getAnnotation(RequestParam.class);
				RequestParams requestParams = new RequestParams(request.getParameters());

				String[] paramValues = requestParams.getArrayParameter(requestParam.value());
				if ((paramValues.length == 0 || paramValues[0].isBlank())
						&& !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
					paramValues = new String[]{requestParam.defaultValue()};
				}

				if (param.getType().equals(String.class)) {
					String stringParam = String.join(",", paramValues);
					params[i] = stringParam;
				} else if (param.getType().isArray()) {
					params[i] = paramValues;
				} else if (converterRegistry.hasConverter(param.getType())) {
					// RequestParams stores values as an array and returns only the
					// first element when getStringParameter is called, so we need to
					// join them back to a string in order to properly pass it to
					// converter for conversion.
					Converter<?> converter = converterRegistry.getConverter(param.getType());
					String stringParam = String.join(",", paramValues);
					params[i] = converter.convert(stringParam);
				} else {
					params[i] = paramValues[0];
				}
			} else if (param.isAnnotationPresent(PathVariable.class)) {
				PathVariable pathVariable = param.getAnnotation(PathVariable.class);
				String value = HttpPathUtil.resolvePathVariableValue(request.getPath(), method.getPath(), pathVariable);
				if (value == null) {
					params[i] = null;
				} else if (converterRegistry.hasConverter(param.getType())) {
					params[i] = converterRegistry.getConverter(param.getType()).convert(value);
				} else {
					params[i] = value;
				}
			} else if (Map.class.isAssignableFrom(param.getType())) {
				params[i] = ((JsonObject) request.getBody()).getData();
			}
		}

		Object result = method.invoke(params);

		if (result == null) {
			String requestContentType = request.getHeader(CONTENT_TYPE);
			response.setBody(null);
			response.setHeader(CONTENT_TYPE, requestContentType == null ? HttpContentType.TEXT_PLAIN : requestContentType);
		} else if (result instanceof View) {
			viewResolver.resolve((View) result, request, response, request.getSession(), SecurityContextHolder.getContext().getAuthentication());
		} else if (result instanceof JsonResponse) {
			response.setStatus(((JsonResponse<?>) result).getStatus());
			response.setBody(((JsonResponse<?>) result).getBody().toJsonString());
			response.addHeaders(((JsonResponse<?>) result).getHeaders());
		} else if (result instanceof JsonString) {
			response.setBody(((JsonString) result).toJsonString());
			response.setHeader(CONTENT_TYPE, HttpContentType.APPLICATION_JSON);
		} else if (result instanceof Object[]) {
			response.setBody(new JsonArray((Object[]) result).toJsonString());
			response.setHeader(CONTENT_TYPE, HttpContentType.APPLICATION_JSON);
		} else if (result instanceof String) {
			if (((String) result).startsWith(REDIRECT_PREFIX)) {
				response.sendRedirect(((String) result).substring(REDIRECT_PREFIX.length()));
			} else {
				response.setBody((String) result);
				if (response.getHeader(CONTENT_TYPE) == null)
					response.setHeader(CONTENT_TYPE, HttpContentType.TEXT_PLAIN);
			}
		} else {
			response.setBody(result.toString());
			if (response.getHeader(CONTENT_TYPE) == null)
				response.setHeader(CONTENT_TYPE, HttpContentType.TEXT_PLAIN);
		}

		// Finally, we need to set the request handled attribute to true
		// so that we don't get 404 exception from the HandlerRunner.
		request.setHandled(true);
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		List<HttpMethod> httpMethods = method.getHttpMethods();
		boolean canHandleMethod = httpMethods.isEmpty() || httpMethods.contains(request.getMethod());
		boolean canHandlePath = HttpPathUtil.arePathsMatching(request.getPath(), method.getPath());

		return canHandleMethod && canHandlePath;
	}
}
