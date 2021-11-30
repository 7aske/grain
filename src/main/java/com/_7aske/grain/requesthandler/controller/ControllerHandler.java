package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.controller.PathVariable;
import com._7aske.grain.controller.RequestParam;
import com._7aske.grain.controller.converter.Converter;
import com._7aske.grain.controller.converter.ConverterRegistry;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpContentType;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.form.FormBody;
import com._7aske.grain.http.form.FormDataMapper;
import com._7aske.grain.http.json.*;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.http.view.DataView;
import com._7aske.grain.http.view.View;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.util.HttpPathUtil;
import com._7aske.grain.util.RequestParams;

import java.lang.reflect.Parameter;
import java.util.Map;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class ControllerHandler implements RequestHandler {
	public static final String REDIRECT_PREFIX = "redirect:";
	private final ControllerWrapper controller;

	public ControllerHandler(ControllerWrapper wrapper) {
		this.controller = wrapper;
	}

	@Override
	public boolean handle(HttpRequest request, HttpResponse response, Session session) throws HttpException {
		ControllerMethodWrapper method = controller.getMethod(request.getPath(), request.getMethod())
				.orElseThrow(() -> new HttpException.NotFound(request.getPath()));

		// @CopyPaste from ControllerWrapper
		String fullControllerMapping = HttpPathUtil.join(controller.getPath(), method.getPath());

		ConverterRegistry converterRegistry = ApplicationContextHolder.getContext().getGrain(ConverterRegistry.class);

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
				params[i] = session;
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
				String paramName = requestParam.value();
				if (param.getType().equals(String.class)) {
					String stringParam = String.join(",", requestParams.getArrayParameter(paramName));
					params[i] = stringParam;
				} else if (param.getType().isArray()) {
					params[i] = requestParams.getArrayParameter(paramName);
				} else if (converterRegistry.hasConverter(param.getType())) {
					// RequestParams stores values as an array and returns only the
					// first element when getStringParameter is called, so we need to
					// join them back to a string in order to properly pass it to
					// converter for conversion.
					Converter<?> converter = converterRegistry.getConverter(param.getType());
					String[] requestParamArray = requestParams.getArrayParameter(paramName);
					if (requestParamArray != null) {
						String stringParam = String.join(",", requestParamArray);
						params[i] = converter.convert(stringParam);
					} else {
						params[i] = converter.convert("");
					}
				} else {
					params[i] = requestParams.getStringParameter(paramName);
				}
			} else if (param.isAnnotationPresent(PathVariable.class)) {
				PathVariable pathVariable = param.getAnnotation(PathVariable.class);
				String value = HttpPathUtil.resolvePathVariableValue(request.getPath(), fullControllerMapping, pathVariable);
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

		Object result = method.invoke(controller.getInstance(), params);

		if (result == null) {
			response.setBody(null);
			response.setHeader(CONTENT_TYPE, "text/plain");
		} else if (result instanceof DataView) {
			// Setting implicit objects
			((DataView) result).setData("request", request);
			((DataView) result).setData("response", response);
			((DataView) result).setData("session", session);
			((DataView) result).setData("authentication", SecurityContextHolder.getContext().getAuthentication());
			response.setBody(((View) result).getContent());
			response.setHeader(CONTENT_TYPE, ((DataView) result).getContentType());
		} else if (View.class.isAssignableFrom(result.getClass())) {
			response.setBody(((View) result).getContent());
			response.setHeader(CONTENT_TYPE, ((View) result).getContentType());
		} else if (result instanceof JsonResponse) {
			response.setStatus(((JsonResponse<?>) result).getStatus());
			response.setBody(((JsonResponse<?>) result).getBody().toJsonString());
			response.addHeaders(((JsonResponse<?>) result).getHeaders());
		} else if (result instanceof JsonObject) {
			response.setBody(((JsonObject) result).toJsonString());
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
		return true;
	}

	@Override
	public boolean canHandle(String path, HttpMethod method) {
		return controller.getMethod(path, method).isPresent();
	}

	@Override
	public String getPath() {
		return controller.getPath();
	}
}
