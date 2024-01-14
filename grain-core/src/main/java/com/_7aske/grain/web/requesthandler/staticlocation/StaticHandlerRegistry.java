package com._7aske.grain.web.requesthandler.staticlocation;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.factory.HandlerProxyFactory;

import java.util.List;

@Grain
@Order(257)
public class StaticHandlerRegistry implements HandlerRegistry {
	private final List<RequestHandler> handlers;

    public StaticHandlerRegistry(StaticLocationsRegistry locationsRegistry, HandlerProxyFactory proxyFactory) {
        this.handlers = locationsRegistry.getStaticLocations()
				.stream()
				.<RequestHandler>map(StaticLocationHandler::new)
				.map(proxyFactory::createProxy)
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		handlers.stream()
				.filter(h -> h.canHandle(request))
				.findFirst()
				.ifPresent(h -> h.handle(request, response));
	}

	@Override
	public List<RequestHandler> getHandlers() {
		return handlers;
	}
}
