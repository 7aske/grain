package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.requesthandler.HandlerRegistry;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerRegistry implements HandlerRegistry {
	private final List<ControllerHandler> controllers;

	public ControllerRegistry(GrainRegistry grainRegistry) {
		this.controllers = grainRegistry.getControllers()
				.stream()
				.map(ControllerWrapper::new)
				.map(ControllerHandler::new)
				.collect(Collectors.toList());
	}

	public Optional<ControllerHandler> getControllerForPath(String path) {
		return controllers
				.stream()
				.filter(c -> c.getWrapper().getMethod(path).isPresent())
				.findFirst();
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		return getControllerForPath(request.getPath()).isPresent();
	}
}
