package com._7aske.grain.web.requesthandler.handler;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.web.http.GrainRequestHandlerException;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.ControllerMethodHandler;
import com._7aske.grain.web.requesthandler.middleware.MiddlewareHandler;
import com._7aske.grain.web.requesthandler.staticlocation.StaticLocationHandler;

import java.util.Collection;
import java.util.Comparator;

/**
 * RequestHandler defines a component interface that is responsible for handling
 * an incoming {@link HttpRequest} and {@link HttpResponse}. RequestHandlers are
 * typically initialized and registered inside a {@link HandlerRegistry}.
 *
 * @see HandlerRegistry
 * @see MiddlewareHandler
 * @see ControllerMethodHandler
 * @see StaticLocationHandler
 */
public interface RequestHandler extends Comparable<RequestHandler> {
	/**
	 * Method for handling and processing the {@link HttpRequest} and writing the resulting
	 * data to the {@link HttpResponse}.
	 *
	 * @param request  the {@link HttpRequest} to be handled.
	 * @param response the {@link HttpResponse} to be handled.
	 */
	void handle(HttpRequest request, HttpResponse response) throws GrainRequestHandlerException;

	/**
	 * Called after the request has been handled.
	 *
	 * @param request the {@link HttpRequest} to be handled.
	 * @param response the {@link HttpResponse} to be handled.
	 */
	default void afterHandle(HttpRequest request, HttpResponse response) {
		// default implementation
	}

	/**
	 * Defines whether this particular RequestHandler is eligible for handling
	 * the provided request. Typically, this method checks whether the handler
	 * has the appropriate path configured that matches the request's path.
	 *
	 * @param request the {@link HttpRequest} to be handled.
	 * @return true if the RequestHandler is eligible for handling the request, false otherwise.
	 */
	boolean canHandle(HttpRequest request);

	/**
	 * Returns the calculated path for the handler. Used when deciding which
	 * handler should be used when there are multiple matches.
	 *
	 * @return Path of the handler.
	 */
	@NotNull String getPath();

	Collection<HttpMethod> getMethods();

	/**
	 * Comparator that sorts request handlers by their path length in
	 * descending order.
	 *
	 * @return o the request handler to compare to.
	 */
	default int compareTo(RequestHandler o) {
		return Comparator.comparingInt((RequestHandler h) -> h.getPath().length())
				.compare(this, o);
	}
}
