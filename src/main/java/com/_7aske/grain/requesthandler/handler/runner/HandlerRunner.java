package com._7aske.grain.requesthandler.handler.runner;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HandlerRunner<T extends HandlerRegistry> {
	private final List<T> handlerRegistries;

	protected HandlerRunner(List<T> handlerRegistries) {
		this.handlerRegistries = handlerRegistries;
	}

	public HandlerRunner<T> addRegistry(T registry) {
		this.handlerRegistries.add(registry);
		return this;
	}

	public void run(HttpRequest request, HttpResponse response, Session session) {
		for(T registry : handlerRegistries) {
			List<Handler> handlers = registry.getHandlers(request.getPath(), request.getMethod());
			if (!handlers.isEmpty()) {
				AtomicBoolean handled = new AtomicBoolean(false);
				handlers.forEach(handler -> {
					if (handled.get()) return;
					boolean res = handler.handle(request, response, session);
					if (res) handled.set(true);
				});
				if (handled.get())
					return;
			}
		}

		throw new HttpException.NotFound(request.getPath());
	}
}
