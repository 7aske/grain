package com._7aske.grain.requesthandler.handler;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

/**
 * RequestHandler defines a component interface that is responsible for handling
 * an incoming {@link HttpRequest} and {@link HttpResponse}. RequestHandlers are
 * typically initialized and registered inside a {@link HandlerRegistry}.
 *
 * @see HandlerRegistry
 * @see com._7aske.grain.requesthandler.middleware.MiddlewareHandler
 * @see com._7aske.grain.requesthandler.controller.ControllerMethodHandler
 * @see com._7aske.grain.requesthandler.staticlocation.StaticLocationHandler
 */
public interface RequestHandler {
	/**
	 * Method for handling and processing the {@link HttpRequest} and writing the resulting
	 * data to the {@link HttpResponse}.
	 *
	 * @param request  the {@link HttpRequest} to be handled.
	 * @param response the {@link HttpResponse} to be handled.
	 */
	void handle(HttpRequest request, HttpResponse response);

	/**
	 * Defines whether this particular RequestHandler is eligible for handling
	 * the provided request. Typically, this method checks whether the handler
	 * has the appropriate path configured that matches the request's path.
	 *
	 * @param request the {@link HttpRequest} to be handled.
	 * @return true if the RequestHandler is eligible for handling the request, false otherwise.
	 */
	boolean canHandle(HttpRequest request);
}
