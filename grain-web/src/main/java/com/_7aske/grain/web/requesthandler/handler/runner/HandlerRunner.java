package com._7aske.grain.web.requesthandler.handler.runner;

import com._7aske.grain.util.By;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.exception.HttpException;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.HandlerRegistry;

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
				.sort(By::objectOrder);
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

			if (response.isCommitted()){
				return;
			}
		}

		if (!response.isCommitted()) {
			throw new HttpException.NotFound(request.getPath());
		}
	}
}
