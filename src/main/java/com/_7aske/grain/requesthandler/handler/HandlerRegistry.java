package com._7aske.grain.requesthandler.handler;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

/**
 * HandlerRegistry is a component in charge of configuring and registering {@link RequestHandler}s.
 * HandlerRegistry should be able to receive the {@link HttpRequest} and {@link HttpResponse} and forward them
 * to the appropriate {@link RequestHandler} for processing. HandlerRegistries
 * typically contain more than one {@link RequestHandler}.
 *
 * @see com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry
 * @see com._7aske.grain.requesthandler.middleware.MiddlewareHandlerRegistry
 * @see com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry
 */
public interface HandlerRegistry {
	/**
	 * Method used for accepting and forwarding incoming {@link HttpRequest} and {@link HttpResponse}.
	 *
	 * @param request the {@link HttpRequest} to be handled.
	 * @param response the {@link HttpResponse} to be handled.
	 */
	void handle(HttpRequest request, HttpResponse response);
}
