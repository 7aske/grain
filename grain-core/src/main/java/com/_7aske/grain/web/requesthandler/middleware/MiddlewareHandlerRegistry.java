package com._7aske.grain.web.requesthandler.middleware;

import com._7aske.grain.core.component.*;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.util.By;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.factory.HandlerProxyFactory;

import java.util.List;

/**
 * {@link HandlerRegistry} implementation that uses {@link Middleware}s to handle requests.
 */
@Grain
@Order(255)
public class MiddlewareHandlerRegistry implements HandlerRegistry {
	private List<? extends RequestHandler> handlers;
	private final HandlerProxyFactory handlerProxyFactory;

	/**
	 * Constructs a new {@link MiddlewareHandlerRegistry} instance.
	 *
	 * @param handlerProxyFactory proxy factory used to create proxies for middleware
	 *                            handlers.
	 */
	public MiddlewareHandlerRegistry(HandlerProxyFactory handlerProxyFactory) {
		this.handlerProxyFactory = handlerProxyFactory;
	}

	/**
	 * Used to load all initialized middleware handlers.
	 */
	@AfterInit
	private void getHandlersInternal(DependencyContainer container) {
		// @Note Reference the comment in ControllerHandlerRegistry#getHandlersInternal.
		handlers = container.getGrains(Middleware.class)
				.stream()
				.map(MiddlewareHandler::new)
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		handlers.stream()
				.filter(handler -> handler.canHandle(request))
				// As middleware handlers are pass-through we need to order them
				.sorted(By::objectOrder)
				.forEach(handler -> {
					RequestHandler proxy = handlerProxyFactory.createProxy(handler);
					try {
						proxy.handle(request, response);
					} catch (Exception e) {
						throw new GrainRuntimeException(e);
					}
				});
	}
}
