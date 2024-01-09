package com._7aske.grain.web.requesthandler.controller;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.controller.parameter.ParameterConverterRegistry;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
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
							 ParameterConverterRegistry parameterConverterRegistry,
							 ResponseWriterRegistry responseWriterRegistry) {
		this.wrapper = wrapper;
		this.methodHandlers = wrapper.getMethods().stream()
				.map(method -> new ControllerMethodHandler(method, parameterConverterRegistry, responseWriterRegistry))
				.toList();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws Exception {
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

		if (handler.isEmpty()) {
			return;
		}

		handler.get().handle(request, response);
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
