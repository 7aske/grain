package com._7aske.grain.requesthandler.staticlocation;

import com._7aske.grain.http.HttpMethod;
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
	public boolean canHandle(String path, HttpMethod method) {
		return getHandler(path, method).isPresent();
	}

	@Override
	public Optional<RequestHandler> getHandler(String path, HttpMethod method) {
		return handlers.stream()
				.filter(handler -> handler.canHandle(path, method))
				.findFirst();
	}
}
