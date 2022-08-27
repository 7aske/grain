package com._7aske.grain.requesthandler.middleware;

import com._7aske.grain.core.component.AfterInit;
import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.util.ReflectionUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link HandlerRegistry} implementation that uses {@link Middleware}s to handle requests.
 */
@Grain
@Order(255)
public class MiddlewareHandlerRegistry implements HandlerRegistry {
	private List<RequestHandler> handlers;
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
				.collect(Collectors.toList());
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		handlers.stream()
				.filter(handler -> handler.canHandle(request))
				// As middleware handlers are pass-through we need to order them
				.sorted((o1, o2) -> ReflectionUtil.sortByOrder(o1.getClass(), o2.getClass()))
				.forEach(handler -> {
					RequestHandler proxy = handlerProxyFactory.createProxy(handler);
					try {
						proxy.handle(request, response);
					} catch (IOException e) {
						throw new GrainRuntimeException();
					}
				});
	}
}
