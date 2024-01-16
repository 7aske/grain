package com._7aske.grain.web.requesthandler.middleware;

import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.component.Ordered;
import com._7aske.grain.web.util.HttpPathUtil;
import com._7aske.grain.web.controller.annotation.Mappings;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.staticlocation.StaticHandlerRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link RequestHandler} implementation responsible for integrating {@link Middleware} instances.
 * Middleware is a class that can be used to intercept the request before it reaches the
 * {@link ControllerHandlerRegistry} or
 * {@link StaticHandlerRegistry} and alter
 * it. Middleware classes must be defined as Grains in order to be loaded by the
 * framework.
 */
public class MiddlewareHandler implements RequestHandler, Ordered {
	private final Middleware middleware;
	private final List<HttpMethod> methods;
	private final String path;
	private final int order;

	/**
	 * Constructs a new {@link MiddlewareHandler} instance.
	 * @param middleware the {@link Middleware} that is the underlying handler.
	 */
	public MiddlewareHandler(Middleware middleware) {
		this.middleware = middleware;
		this.order = Optional.ofNullable(middleware.getClass().getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);
		this.methods = Arrays.asList(Mappings.getAnnotatedHttpMethods(middleware.getClass(), HttpMethod.values()));
		this.path= Mappings.getAnnotatedHttpPath(middleware.getClass(), "/");
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		middleware.handle(request, response);
	}

	@Override
	public boolean canHandle(HttpRequest request) {
//		if (request.isHandled()) return false;
		boolean methodMatch = methods.isEmpty() || methods.contains(request.getMethod());
		boolean pathMatch = path == null || path.isBlank() || HttpPathUtil.antMatching(path, request.getPath());
		return methodMatch && pathMatch;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Collection<HttpMethod> getMethods() {
		return methods;
	}

	@Override
	public int getOrder() {
		return order;
	}
}
