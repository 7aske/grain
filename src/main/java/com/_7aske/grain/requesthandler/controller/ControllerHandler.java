package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.json.*;
import com._7aske.grain.http.view.AbstractView;
import com._7aske.grain.http.view.DataView;
import com._7aske.grain.requesthandler.handler.RequestHandler;

import java.lang.reflect.Parameter;
import java.util.Map;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class ControllerHandler implements RequestHandler {
	private final ControllerWrapper controller;

	public ControllerHandler(ControllerWrapper wrapper) {
		this.controller = wrapper;
	}

	@Override
	public boolean handle(HttpRequest request, HttpResponse response) throws HttpException {
		ControllerMethodWrapper method = controller.getMethod(request.getPath(), request.getMethod())
				.orElseThrow(() -> new HttpException.NotFound(request.getPath()));

		Parameter[] declaredParams = method.getParameters();
		Object[] params = new Object[declaredParams.length];
		for (int i = 0; i < declaredParams.length; i++) {
			Parameter param = declaredParams[i];
			if (param.getType().equals(HttpRequest.class)) {
				params[i] = request;
			} else if (param.getType().equals(HttpResponse.class)) {
				params[i] = response;
			} else if (param.isAnnotationPresent(JsonBody.class)) {
				params[i] = new JsonSerializer<>(param.getType()).serialize((JsonObject) request.getBody());
			} else if (Map.class.isAssignableFrom(param.getType())) {
				params[i] = ((JsonObject)request.getBody()).getData();
			}
		}

		Object result = method.invoke(controller.getInstance(), params);

		if (result == null) {
			response.setBody(null);
			response.setHeader(CONTENT_TYPE, "text/plain");
		} else if (result instanceof DataView) {
			((DataView) result).setData("request", request);
			((DataView) result).setData("response", response);
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
			response.setHeader(CONTENT_TYPE, "application/json");
		} else if (result instanceof Object[]) {
			response.setBody(new JsonArray((Object[]) result).toJsonString());
			response.setHeader(CONTENT_TYPE, "application/json");
		} else if (result instanceof String) {
			response.setBody((String) result);
			response.setHeader(CONTENT_TYPE, "text/plain");
		} else {
			response.setBody(result.toString());
			response.setHeader(CONTENT_TYPE, "text/plain");
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
