package com._7aske.grain.web.requesthandler.middleware;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
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
	private final List<RequestHandler> handlers;

	/**
	 * Constructs a new {@link MiddlewareHandlerRegistry} instance.
	 *
	 * @param proxyFactory proxy factory used to create proxies for middleware
	 *                            handlers.
	 */
	public MiddlewareHandlerRegistry(HandlerProxyFactory proxyFactory, List<Middleware> middlewares) {
		this.handlers = middlewares.stream()
				.map(MiddlewareHandler::new)
				.sorted(By.order())
				.map(proxyFactory::createProxy)
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		handlers.stream()
				.filter(h -> h.canHandle(request))
				.forEach(h -> h.handle(request, response));
	}

    @Override
    public List<RequestHandler> getHandlers() {
        return handlers;
    }
}
