package com._7aske.grain.requesthandler.middleware;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.util.HttpPathUtil;
import com._7aske.grain.util.ReflectionUtil;

import java.util.Arrays;
import java.util.List;

/**
 * {@link RequestHandler} implementation responsible for integrating {@link Middleware} instances.
 * Middleware is a class that can be used to intercept the request before it reaches the
 * {@link com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry} or
 * {@link com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry} and alter
 * it. Middleware classes must be defined as Grains in order to be loaded by the
 * framework.
 */
public class MiddlewareHandler implements RequestHandler {
	private final Middleware middleware;
	private final List<HttpMethod> methods;
	private final String path;

	/**
	 * Constructs a new {@link MiddlewareHandler} instance.
	 * @param middleware the {@link Middleware} that is the underlying handler.
	 */
	public MiddlewareHandler(Middleware middleware) {
		this.middleware = middleware;
		HttpMethod[] httpMethods = new HttpMethod[0];
		try {
			httpMethods = ReflectionUtil.getAnnotatedHttpMethods(middleware.getClass());
		} catch (GrainRuntimeException ex) {
			// Throw happens if the middleware was not annotated with @RequestMapping.
			// Middleware doesn't have to be annotated with this annotation but
			// the method used for resolving RequestMapping annotations is used for
			// controllers mainly where it is required. This should be changed in
			// the future.
		}
		this.methods = Arrays.asList(httpMethods);
		this.path = ReflectionUtil.getAnnotatedHttpPath(middleware.getClass());
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		middleware.handle(request, response);
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		if (request.isHandled()) return false;
		boolean methodMatch = methods.isEmpty() || methods.contains(request.getMethod());
		boolean pathMatch = path == null || path.isBlank() || HttpPathUtil.antMatching(path, request.getPath());
		return methodMatch && pathMatch;
	}
}
