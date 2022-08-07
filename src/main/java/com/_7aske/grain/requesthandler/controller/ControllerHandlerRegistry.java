package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.core.component.Controller;
import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;

import java.util.List;
import java.util.stream.Collectors;

@Grain
public class ControllerHandlerRegistry implements HandlerRegistry {
	@Inject
	private DependencyContainer container;
	private List<ControllerHandler> handlers = null;

	public List<Handler> getHandlers(String path, HttpMethod method) {
		// @Warning this means that new handlers cannot be registered at runtime
		if (handlers == null) {
			handlers = container.getGrainsAnnotatedBy(Controller.class)
					.stream()
					.map(ControllerWrapper::new)
					.map(ControllerHandler::new)
					.collect(Collectors.toList());
		}
		return handlers
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
