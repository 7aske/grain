package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.core.component.*;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.json.JsonMapper;
import com._7aske.grain.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.view.ViewResolver;
import com._7aske.grain.web.view.ViewResolverProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * {@link HandlerRegistry} that dispatches requests to {@link ControllerHandler}s.
 */
@Grain
@Order(256)
public class ControllerHandlerRegistry implements HandlerRegistry {
	private List<RequestHandler> handlers = new ArrayList<>();
	private final ConverterRegistry converterRegistry;
	private final ViewResolver viewResolver;
	private final HandlerProxyFactory handlerProxyFactory;
	private final JsonMapper jsonMapper;

	public ControllerHandlerRegistry(HandlerProxyFactory handlerProxyFactory,
									 ConverterRegistry converterRegistry,
									 ViewResolverProvider viewResolver,
									 JsonMapper jsonMapper) {
		this.handlerProxyFactory = handlerProxyFactory;
		this.converterRegistry = converterRegistry;
		this.viewResolver = viewResolver;
		this.jsonMapper = jsonMapper;
	}

	@AfterInit
	private void getHandlersInternal(DependencyContainer container) {
		// @Refactor this is a temporary solution. If this class is instantiated
		// during the dependency injection pipeline controllers cannot be fetched from
		// dependency container because we can not guarantee that they are initialized.
		// We have to "inject" them manually after the whole initialization process is finished.
		// We could inject a list of controllers but then we would have to have them
		// implement an interface or extend a base class. Alternatively, we could
		// inject them by name and derive the name using the @Controller annotation.
		handlers = container.getGrainsAnnotatedBy(Controller.class)
				.stream()
				.map(ControllerWrapper::new)
				.map(wrapper -> new ControllerHandler(wrapper, converterRegistry, viewResolver, jsonMapper))
				.collect(Collectors.toList());
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
