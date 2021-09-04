package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpHeaders;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.json.JsonBody;
import com._7aske.grain.http.json.JsonObject;
import com._7aske.grain.http.json.JsonSerializer;
import com._7aske.grain.http.view.AbstractView;
import com._7aske.grain.requesthandler.RequestHandler;

import java.lang.reflect.Parameter;
import java.util.Map;

public class ControllerHandler implements RequestHandler {
	private final ControllerWrapper controller;

	public ControllerHandler(ControllerWrapper wrapper) {
		this.controller = wrapper;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws HttpException {
		ControllerMethodWrapper method = controller.getMethod(request.getPath(), request.getMethod())
				.orElseThrow(HttpException.NotFound::new);

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

		if (result instanceof AbstractView) {
			response.setBody(((AbstractView) result).getContent());
			response.setHeader(HttpHeaders.CONTENT_TYPE, ((AbstractView) result).getContentType());
		} else if (result instanceof JsonObject) {
			response.setBody(((JsonObject) result).toJsonString());
			response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		} else if (result instanceof String) {
			response.setBody((String) result);
			response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");

		} else {
			response.setBody(result.toString());
			response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
		}
	}

	@Override
	public boolean canHandle(String path) {
		return controller.getMethod(path).isPresent();
	}

	@Override
	public String getPath() {
		return controller.getPath();
	}
}
