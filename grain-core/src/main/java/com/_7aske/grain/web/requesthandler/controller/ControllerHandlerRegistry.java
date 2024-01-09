package com._7aske.grain.web.requesthandler.controller;

import com._7aske.grain.core.component.*;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.controller.parameter.ParameterConverterRegistry;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.web.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.factory.HandlerProxyFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * {@link HandlerRegistry} that dispatches requests to {@link ControllerHandler}s.
 */
@Grain
@Order(256)
public class ControllerHandlerRegistry implements HandlerRegistry {
	private final List<RequestHandler> handlers;
	private final HandlerProxyFactory handlerProxyFactory;

	public ControllerHandlerRegistry(HandlerProxyFactory handlerProxyFactory,
                                     ParameterConverterRegistry parameterConverterRegistry,
									 ResponseWriterRegistry responseWriterRegistry,
									 @AnnotatedBy(Controller.class) List<Object> controllers) {
		this.handlerProxyFactory = handlerProxyFactory;
		this.handlers = controllers.stream()
				.map(ControllerWrapper::new)
				.<RequestHandler>map(wrapper -> new ControllerHandler(wrapper, parameterConverterRegistry, responseWriterRegistry))
				.toList();
    }

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		// @CopyPasta
		List<RequestHandler> availableHandlers = this.handlers.stream()
				.filter(handler -> handler.canHandle(request))
				.sorted(Comparator.comparingInt((ToIntFunction<? super RequestHandler>)
						h -> h.getPath().length()).reversed())
				.toList();

		Optional<RequestHandler> handler = Optional.empty();
		if (availableHandlers.size() == 1) {
			handler = Optional.of(availableHandlers.get(0));
		} else if (availableHandlers.size() > 1) {
			handler = Optional.ofNullable(availableHandlers.stream()
					.filter(h -> h.getPath().equals(request.getPath()))
					.findFirst()
					.orElse(availableHandlers.get(0)));
		}

		handler.ifPresent(h -> {
			RequestHandler proxy = handlerProxyFactory.createProxy(h);
			try {
				proxy.handle(request, response);
			} catch (IOException e) {
				throw new GrainRuntimeException(e);
			}
		});
	}
}
