package com._7aske.grain.requesthandler.handler.runner;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.requesthandler.middleware.Middleware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HandlerRunner implements Handler {
	private final List<HandlerRegistry> handlerRegistries;
	private final HandlerProxyFactory proxyFactory;

	protected HandlerRunner(HandlerProxyFactory proxyFactory) {
		this.handlerRegistries = new ArrayList<>();
		this.proxyFactory = proxyFactory;
	}

	public <T extends HandlerRegistry> HandlerRunner addRegistry(HandlerRegistry registry) {
		this.handlerRegistries.add(registry);
		return this;
	}

	public boolean handle(HttpRequest request, HttpResponse response, Session session) {
		for (HandlerRegistry registry : handlerRegistries) {
			List<Handler> handlers = registry.getHandlers(request.getPath(), request.getMethod());
			if (!handlers.isEmpty()) {
				AtomicBoolean handled = new AtomicBoolean(false);
				handlers.forEach(handler -> {
					if (handled.get()) return;
					boolean res = false;
					// We don't proxy middlewares because proxy throws
					// security exception.
					if (Middleware.class.isAssignableFrom(handler.getClass())) {
						res = handler.handle(request, response, session);
					} else {
						Handler proxy = proxyFactory.createProxy(handler);
						res = proxy.handle(request, response, session);
					}
					if (res) handled.set(true);
				});
				if (handled.get())
					return true;
			}
		}

		throw new HttpException.NotFound(request.getPath());
	}
}
