package com._7aske.grain.web.requesthandler.middleware;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.web.requesthandler.staticlocation.StaticHandlerRegistry;

/**
 * Middleware is a component that can be used to intercept the request before it
 * reaches the {@link ControllerHandlerRegistry} or
 * {@link StaticHandlerRegistry} and alter
 * it. Middleware classes must be defined as Grains in order to be loaded by the
 * framework.
 *
 * @see MiddlewareHandler
 * @see MiddlewareHandlerRegistry
 */
public interface Middleware {
	void handle(HttpRequest request, HttpResponse response);
}