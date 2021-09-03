package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpHeaders;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.json.JsonObject;
import com._7aske.grain.http.view.AbstractView;
import com._7aske.grain.requesthandler.RequestHandler;

import java.util.Arrays;

import static com._7aske.grain.util.ArrayUtil.swap;

public class ControllerHandler implements RequestHandler {
	private final ControllerWrapper controller;

	public ControllerHandler(ControllerWrapper wrapper) {
		this.controller = wrapper;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws HttpException {
		ControllerMethodWrapper method = controller.getMethod(request.getPath(), request.getMethod())
				.orElseThrow(HttpException.NotFound::new);

		Object result = method.invoke(controller.getInstance(), sortedVarArgs(method, request, response));

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

	private Object[] sortedVarArgs(ControllerMethodWrapper method, Object... args) {
		if (method.getParameterCount() == 0) return new Object[0];
		Class<?>[] params = method.getParameterTypes();
		Object[] retval = Arrays.copyOf(args, args.length);

		for (int i = 0; i < params.length; i++) {
			Class<?> clazz = params[i];
			int index = findArgIndexByClass(clazz, retval);
			if (index != -1 && i != index) {
				swap(retval, i, index);
			}
		}

		return Arrays.copyOfRange(retval, 0, method.getParameterCount());
	}

	public int findArgIndexByClass(Class<?> clazz, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].getClass().equals(clazz)) {
				return i;
			}
		}
		return -1;
	}
}
