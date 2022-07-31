package com._7aske.grain.requesthandler.staticlocation;

import com._7aske.grain.core.component.Default;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.requesthandler.handler.RequestHandler;

import java.util.List;
import java.util.stream.Collectors;

@Grain
@Default
public class StaticHandlerRegistry implements HandlerRegistry {
	private final List<RequestHandler> handlers;

	public StaticHandlerRegistry(StaticLocationsRegistry locationsRegistry) {
		this.handlers = locationsRegistry.getStaticLocations()
				.stream()
				.map(StaticLocationHandler::new)
				.collect(Collectors.toList());
	}

	@Override
	public boolean canHandle(String path, HttpMethod method) {
		return !getHandlers(path, method).isEmpty();
	}

	@Override
	public List<Handler> getHandlers(String path, HttpMethod method) {
		return handlers.stream()
				.filter(handler -> handler.canHandle(path, method))
				.map(Handler.class::cast)
				.collect(Collectors.toList());
	}
}
