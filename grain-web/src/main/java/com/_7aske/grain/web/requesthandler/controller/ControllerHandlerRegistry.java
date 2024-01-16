package com._7aske.grain.web.requesthandler.controller;

import com._7aske.grain.web.controller.annotation.Controller;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.util.RequestHandlerUtil;
import com._7aske.grain.web.controller.parameter.ParameterConverterRegistry;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.web.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.factory.HandlerProxyFactory;

import java.util.List;

/**
 * {@link HandlerRegistry} that dispatches requests to {@link ControllerHandler}s.
 */
@Grain
@Order(256)
public class ControllerHandlerRegistry implements HandlerRegistry {
	private final List<RequestHandler> handlers;
	private final List<ControllerHandler> controllers;

	public ControllerHandlerRegistry(HandlerProxyFactory handlerProxyFactory,
                                     ParameterConverterRegistry parameterConverterRegistry,
									 ResponseWriterRegistry responseWriterRegistry,
									 @Inject(annotatedBy = Controller.class) List<Object> controllers) {
		this.controllers = controllers.stream()
				.map(ControllerWrapper::new)
				.map(wrapper -> new ControllerHandler(wrapper, parameterConverterRegistry, responseWriterRegistry))
				.toList();

		this.handlers = this.controllers.stream()
				.flatMap(controllerHandler -> controllerHandler.getHandlers().stream())
				.map(handlerProxyFactory::createProxy)
				.toList();
    }

	public List<ControllerHandler> getControllers() {
		return controllers;
	}

	public List<RequestHandler> getHandlers() {
		return handlers;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		RequestHandlerUtil.getBestHandler(request, this.handlers)
				.ifPresent(h -> h.handle(request, response));
	}
}
