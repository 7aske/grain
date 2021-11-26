package com._7aske.grain.http.session;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.component.Priority;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.middleware.Middleware;

import java.util.Objects;
import java.util.UUID;

@Grain
@Priority(0)
public class SessionMiddleware implements Middleware {
	@Inject
	private Configuration configuration;

	// @Bug sets the cookie for when the server requests
	// resources like favicon
	@Override
	public boolean handle(HttpRequest request, HttpResponse response) {
		if (!Objects.equals(configuration.getProperty(Configuration.Key.SESSION_ENABLED), true)) {
			return false;
		}
		if (request.getCookie() == null) {
			response.setCookie(new Cookie("GSID", UUID.randomUUID().toString()));
		}
		return false;
	}
}
