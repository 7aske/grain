package com._7aske.grain.requesthandler.middleware;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

/**
 * Middleware is a component that can be used to intercept the request before it
 * reaches the {@link com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry} or
 * {@link com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry} and alter
 * it. Middleware classes must be defined as Grains in order to be loaded by the
 * framework.
 *
 * @see MiddlewareHandler
 * @see MiddlewareHandlerRegistry
 */
public interface Middleware {
	void handle(HttpRequest request, HttpResponse response);
}