package com._7aske.grain.requesthandler.staticlocation;

import com._7aske.grain.requesthandler.HandlerRegistry;
import com._7aske.grain.requesthandler.RequestHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaticHandlerRegistry implements HandlerRegistry {
	private final List<RequestHandler> handlers;

	public StaticHandlerRegistry(StaticLocationsRegistry locationsRegistry) {
		this.handlers = locationsRegistry.getStaticLocations()
				.stream()
				.map(StaticLocationHandler::new)
				.collect(Collectors.toList());
	}

	@Override
	public boolean canHandle(String path) {
		return getHandler(path).isPresent();
	}

	@Override
	public Optional<RequestHandler> getHandler(String path) {
		return handlers.stream()
				.filter(handler -> handler.canHandle(path))
				.findFirst();
	}
}
