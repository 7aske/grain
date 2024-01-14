package com._7aske.grain.web.requesthandler.controller;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.util.RequestHandlerUtil;
import com._7aske.grain.web.controller.parameter.ParameterConverterRegistry;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RequestHandler} implementation that wraps a {@link com._7aske.grain.core.component.Controller} instance.
 */
public class ControllerHandler implements RequestHandler {
	/**
	 * Wrapped controller methods.
	 */
	private final List<ControllerMethodHandler> handlers;
	private final ControllerWrapper wrapper;

	public ControllerHandler(ControllerWrapper wrapper,
							 ParameterConverterRegistry parameterConverterRegistry,
							 ResponseWriterRegistry responseWriterRegistry) {
		this.wrapper = wrapper;
		this.handlers = wrapper.getMethods().stream()
				.map(method -> new ControllerMethodHandler(method, parameterConverterRegistry, responseWriterRegistry))
				.toList();
	}

	public List<RequestHandler> getHandlers() {
		return handlers.stream()
				.map(RequestHandler.class::cast)
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		RequestHandlerUtil.getBestHandler(request, handlers)
				.ifPresent(h -> h.handle(request, response));
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		return handlers.stream()
				.anyMatch(methodHandler -> methodHandler.canHandle(request));
	}

	@Override
	public @NotNull String getPath() {
		return wrapper.getPath();
	}

	@Override
	public Collection<HttpMethod> getMethods() {
		return handlers.stream()
				.flatMap(methodHandler -> methodHandler.getMethods().stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public String toString() {
		return "%s(%s)".formatted(wrapper.getName(), wrapper.getPath());
	}
}
