package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.controller.PathVariable;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpContentType;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.form.FormBody;
import com._7aske.grain.http.form.FormDataMapper;
import com._7aske.grain.http.json.*;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.http.view.AbstractView;
import com._7aske.grain.http.view.DataView;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.util.RequestParams;
import com._7aske.grain.util.HttpPathUtil;

import java.lang.reflect.Parameter;
import java.util.Map;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class ControllerHandler implements RequestHandler {
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
			} else if (param.isAnnotationPresent(PathVariable.class)) {
				PathVariable pathVariable = param.getAnnotation(PathVariable.class);
				String value = HttpPathUtil.resolvePathVariableValue(request.getPath(), fullControllerMapping, pathVariable);
				// @Refactor move this to converter registry
				if (value == null) {
					params[i] = null;
				} else if (Integer.class.isAssignableFrom(param.getType())) {
					params[i] = Integer.parseInt(value);
				} else if (Float.class.isAssignableFrom(param.getType())) {
					params[i] = Float.parseFloat(value);
				} else if (Long.class.isAssignableFrom(param.getType())) {
					params[i] = Long.parseLong(value);
				} else if (Boolean.class.isAssignableFrom(param.getType())) {
					params[i] = Boolean.parseBoolean(value);
				} else if (Short.class.isAssignableFrom(param.getType())) {
					params[i] = Short.parseShort(value);
				} else if (Byte.class.isAssignableFrom(param.getType())) {
					params[i] = Byte.parseByte(value);
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
			((DataView) result).setData("request", request);
			((DataView) result).setData("response", response);
			((DataView) result).setData("session", session);
			response.setBody(((AbstractView) result).getContent());
			response.setHeader(CONTENT_TYPE, ((AbstractView) result).getContentType());
		} else if (result instanceof AbstractView) {
			response.setBody(((AbstractView) result).getContent());
			response.setHeader(CONTENT_TYPE, ((AbstractView) result).getContentType());
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
			response.setBody((String) result);
			if (response.getHeader(CONTENT_TYPE) == null)
				response.setHeader(CONTENT_TYPE, HttpContentType.TEXT_PLAIN);
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
