package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpHeaders;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.json.JsonBody;
import com._7aske.grain.http.json.JsonObject;
import com._7aske.grain.http.json.JsonResponse;
import com._7aske.grain.http.json.JsonSerializer;
import com._7aske.grain.http.view.AbstractView;
import com._7aske.grain.requesthandler.handler.RequestHandler;

import java.lang.reflect.Parameter;
import java.util.Map;

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

		if (result instanceof AbstractView) {
			response.setBody(((AbstractView) result).getContent());
			response.setHeader(HttpHeaders.CONTENT_TYPE, ((AbstractView) result).getContentType());
		} else if (result instanceof JsonResponse) {
			response.setBody(((JsonResponse<?>) result).getBody().toJsonString());
			response.addHeaders(((JsonResponse<?>) result).getHeaders());
			response.setStatus(((JsonResponse<?>) result).getStatus());
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
