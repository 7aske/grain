package com._7aske.grain.handler;

import com._7aske.grain.component.ControllerMethodWrapper;
import com._7aske.grain.component.ControllerRegistry;
import com._7aske.grain.component.ControllerWrapper;
import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpRequestParser;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;

public class RequestHandler implements Runnable {
	private final Socket socket;
	private final GrainRegistry registry;
	private final ControllerRegistry controllerRegistry;

	public RequestHandler(GrainRegistry registry, Socket socket) {
		this.socket = socket;
		this.registry = registry;
		this.controllerRegistry = new ControllerRegistry(registry);
	}

	public static RequestHandler handle(GrainRegistry registry, Socket socket) {
		return new RequestHandler(registry, socket);
	}

	private Object doRun(HttpRequest request, HttpResponse response) throws HttpException {
		Optional<ControllerWrapper> controllerOptional = controllerRegistry.getControllerForPath(request.getPath());
		ControllerWrapper controller = controllerOptional.orElseThrow(HttpException.NotFound::new);
		Optional<ControllerMethodWrapper> handlerMethod = controller.getHandlerForPathAndMethod(request.getPath(), request.getMethod());
		ControllerMethodWrapper method = handlerMethod.orElseThrow(HttpException.NotFound::new);

		return method.invoke(controller.getInstance(), sortedVarArgs(method, request, response));
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

	private static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	public int findArgIndexByClass(Class<?> clazz, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].getClass().equals(clazz)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void run() {
		try {
			BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			HttpRequestParser parser = new HttpRequestParser(reader);

			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();

			Object result;
			try {
				result = doRun(request, response);
				response.setStatus(HttpStatus.OK);
				response.setBody((String) result);
			} catch (HttpException ex) {
				response.setStatus(ex.getStatus());
				response.setBody(ex.getMessage());
			}
			writer.write(response.getHttpString());
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			throw new HttpException.InternalServerError(e);
		}
	}
}
