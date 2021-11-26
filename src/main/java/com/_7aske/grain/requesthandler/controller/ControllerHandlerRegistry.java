package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.component.Inject;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;

import java.util.List;
import java.util.stream.Collectors;

@Grain
public class ControllerHandlerRegistry implements HandlerRegistry {
	@Inject
	private GrainRegistry grainRegistry;

	public List<Handler> getHandlers(String path, HttpMethod method) {
		return grainRegistry.getControllers()
				.stream()
				.map(ControllerWrapper::new)
				.map(ControllerHandler::new)
				.filter(c -> c.canHandle(path, method))
				.map(Handler.class::cast)
				.collect(Collectors.toList());
	}

	@Override
	public boolean canHandle(String path, HttpMethod method) {
		return !getHandlers(path, method).isEmpty();
	}
}
