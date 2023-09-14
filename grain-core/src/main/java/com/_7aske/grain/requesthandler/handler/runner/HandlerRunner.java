package com._7aske.grain.requesthandler.handler.runner;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.util.ReflectionUtil;

import java.util.List;

/**
 * HandlerRunner used to run the registry handlers in the order they are registered.
 */
@Grain
public class HandlerRunner {
	private final List<HandlerRegistry> handlerRegistries;

	protected HandlerRunner(List<HandlerRegistry> registries) {
		this.handlerRegistries = registries;
	}

	/**
	 * Adds a registry to the list of registries to run for each request.
	 *
	 * @param registry the registry to add
	 * @return this
	 */
	public HandlerRunner addRegistry(HandlerRegistry registry) {
		this.handlerRegistries.add(registry);
		// We must take into the account the ordering of the registries if
		// we decide to add a new one at runtime
		this.handlerRegistries
				.sort((o1, o2) -> ReflectionUtil.sortByOrder(o1.getClass(), o2.getClass()));
		return this;
	}

	/**
	 * Runs the handler registries in the order they are registered.
	 *
	 * @param request  the request to handle
	 * @param response the response to write to
	 */
	public void handle(HttpRequest request, HttpResponse response) {
		for (HandlerRegistry registry : handlerRegistries) {
			registry.handle(request, response);
		}

		if (!request.isHandled())
			throw new HttpException.NotFound(request.getPath());
	}
}
