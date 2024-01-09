package com._7aske.grain.web.requesthandler.staticlocation;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.factory.HandlerProxyFactory;

import java.io.IOException;
import java.util.List;

@Grain
@Order(257)
public class StaticHandlerRegistry implements HandlerRegistry {
	private final List<? extends RequestHandler> handlers;
	private final HandlerProxyFactory proxyFactory;

	public StaticHandlerRegistry(StaticLocationsRegistry locationsRegistry, HandlerProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
		this.handlers = locationsRegistry.getStaticLocations()
				.stream()
				.map(StaticLocationHandler::new)
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		handlers.stream()
				.filter(handler -> handler.canHandle(request))
				.findFirst()
				.ifPresent(handler -> {
					RequestHandler proxy = proxyFactory.createProxy(handler);
					try {
						proxy.handle(request, response);
					} catch (Exception e) {
						throw new GrainRuntimeException(e);
					}
				});
	}
}
