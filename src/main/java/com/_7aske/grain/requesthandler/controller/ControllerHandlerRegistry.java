package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.requesthandler.handler.RequestHandler;

import java.util.List;
import java.util.stream.Collectors;

public class ControllerHandlerRegistry implements HandlerRegistry {
	private final List<RequestHandler> controllers;

	public ControllerHandlerRegistry(GrainRegistry grainRegistry) {
		this.controllers = grainRegistry.getControllers()
				.stream()
				.map(ControllerWrapper::new)
				.map(ControllerHandler::new)
				.collect(Collectors.toList());
	}

	public List<Handler> getHandlers(String path, HttpMethod method) {
		return controllers
				.stream()
				.filter(c -> c.canHandle(path, method))
				.map(Handler.class::cast)
				.collect(Collectors.toList());
	}

	@Override
	public boolean canHandle(String path, HttpMethod method) {
		return !getHandlers(path, method).isEmpty();
	}
}
