package com._7aske.grain.web.requesthandler.handler;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.web.requesthandler.middleware.MiddlewareHandlerRegistry;
import com._7aske.grain.web.requesthandler.staticlocation.StaticHandlerRegistry;

import java.util.List;

/**
 * HandlerRegistry is a component in charge of configuring and registering {@link RequestHandler}s.
 * HandlerRegistry should be able to receive the {@link HttpRequest} and {@link HttpResponse} and forward them
 * to the appropriate {@link RequestHandler} for processing. HandlerRegistries
 * typically contain more than one {@link RequestHandler}.
 *
 * @see ControllerHandlerRegistry
 * @see MiddlewareHandlerRegistry
 * @see StaticHandlerRegistry
 */
public interface HandlerRegistry {
	/**
	 * Method used for accepting and forwarding incoming {@link HttpRequest} and {@link HttpResponse}.
	 *
	 * @param request the {@link HttpRequest} to be handled.
	 * @param response the {@link HttpResponse} to be handled.
	 */
	void handle(HttpRequest request, HttpResponse response);

	/**
	 * Method used for retrieving all registered {@link RequestHandler}s.
	 *
	 * @return a list of all registered {@link RequestHandler}s.
	 */
	List<RequestHandler> getHandlers();

	/**
	 * Called after the request has been handled.
	 *
	 * @param request the {@link HttpRequest} to be handled.
	 * @param response the {@link HttpResponse} to be handled.
	 */
	default void afterHandle(HttpRequest request, HttpResponse response) {
		// default implementation
	}
}
