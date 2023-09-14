package com._7aske.grain.http.session;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

/**
 * Implementing this interface you override the implementation of how the session
 * is initialized from the HttpRequest.
 */
public interface SessionInitializer {
	/**
	 * @param request incoming request
	 * @param response outgoing response
	 * @return initialized session
	 */
	Session initialize(HttpRequest request, HttpResponse response);
}
