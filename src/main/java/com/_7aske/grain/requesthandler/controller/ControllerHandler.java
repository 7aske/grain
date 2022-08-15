package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.view.ViewResolver;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RequestHandler} implementation that wraps a {@link com._7aske.grain.core.component.Controller} instance.
 */
public class ControllerHandler implements RequestHandler {
	/**
	 * Wrapped controller methods.
	 */
	private final List<ControllerMethodHandler> methodHandlers;

	public ControllerHandler(ControllerWrapper wrapper,
	                         ConverterRegistry converterRegistry,
	                         ViewResolver viewResolver) {
		this.methodHandlers = wrapper.getMethods().stream()
				.map(method -> new ControllerMethodHandler(method, converterRegistry, viewResolver))
				.collect(Collectors.toList());
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws HttpException {
		this.methodHandlers.stream()
				.filter(methodHandler -> methodHandler.canHandle(request))
				.findFirst()
				.ifPresent(methodHandler -> methodHandler.handle(request, response));
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		return methodHandlers.stream()
				.anyMatch(methodHandler -> methodHandler.canHandle(request));
	}
}
