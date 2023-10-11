package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.json.JsonMapper;
import com._7aske.grain.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.view.ViewResolver;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * {@link RequestHandler} implementation that wraps a {@link com._7aske.grain.core.component.Controller} instance.
 */
public class ControllerHandler implements RequestHandler {
	/**
	 * Wrapped controller methods.
	 */
	private final List<ControllerMethodHandler> methodHandlers;
	private final ControllerWrapper wrapper;

	public ControllerHandler(ControllerWrapper wrapper,
							 ConverterRegistry converterRegistry,
							 ViewResolver viewResolver,
							 JsonMapper jsonMapper) {
		this.wrapper = wrapper;
		this.methodHandlers = wrapper.getMethods().stream()
				.map(method -> new ControllerMethodHandler(method, converterRegistry, viewResolver, jsonMapper))
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws HttpException {
		List<ControllerMethodHandler> handlers = this.methodHandlers.stream()
				.filter(methodHandler -> methodHandler.canHandle(request))
				.sorted(Comparator.comparingInt((ToIntFunction<? super ControllerMethodHandler>)
						h -> h.getPath().length()).reversed())
				.toList();
		Optional<ControllerMethodHandler> handler = Optional.empty();
		if (handlers.size() == 1) {
			handler = Optional.of(handlers.get(0));
		} else if (handlers.size() > 1) {
			handler = Optional.ofNullable(handlers.stream()
					.filter(h -> h.getPath().equals(request.getPath()))
					.findFirst()
					.orElse(handlers.get(0)));
		}

		handler.ifPresent(methodHandler -> {
			try {
				methodHandler.handle(request, response);
			} catch (IOException e) {
				throw new GrainRuntimeException(e);
			}
		});
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		return methodHandlers.stream()
				.anyMatch(methodHandler -> methodHandler.canHandle(request));
	}

	@Override
	public String getPath() {
		return wrapper.getPath();
	}
}
