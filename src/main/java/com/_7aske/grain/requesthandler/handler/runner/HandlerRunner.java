package com._7aske.grain.requesthandler.handler.runner;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;

import java.util.List;

public class HandlerRunner<T extends HandlerRegistry> {
	private final List<T> handlerRegistries;

	protected HandlerRunner(List<T> handlerRegistries) {
		this.handlerRegistries = handlerRegistries;
	}

	public HandlerRunner<T> addRegistry(T registry) {
		this.handlerRegistries.add(registry);
		return this;
	}

	public void run(HttpRequest request, HttpResponse response) {
		for(T registry : handlerRegistries) {
			List<Handler> handlers = registry.getHandlers(request.getPath(), request.getMethod());
			if (!handlers.isEmpty()) {
				boolean handled = handlers.get(0).handle(request, response);
				if (handled)
					return;
			}
		}

		throw new HttpException.NotFound(request.getPath());
	}
}
