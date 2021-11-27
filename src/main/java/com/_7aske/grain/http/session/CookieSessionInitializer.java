package com._7aske.grain.http.session;

import com._7aske.grain.config.Configuration;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

import java.util.Objects;
import java.util.UUID;

import static com._7aske.grain.config.Configuration.*;
import static com._7aske.grain.http.session.SessionConstants.*;

// @Refactor should be an interface
public class CookieSessionInitializer {
	private final Configuration configuration;
	private final SessionStore sessionStore;

	public CookieSessionInitializer(Configuration configuration, SessionStore sessionStore) {
		this.configuration = configuration;
		this.sessionStore = sessionStore;
	}

	public Session initialize(HttpRequest request, HttpResponse response) {
		if (!Objects.equals(configuration.getProperty(Key.SESSION_ENABLED), true)) {
			return null;
		}

		long maxAge = System.currentTimeMillis() + configuration.getProperty(Key.SESSION_MAX_AGE, SESSION_DEFAULT_MAX_AGE);

		Cookie gsid = request.getCookie(SESSION_COOKIE_NAME);
		if (gsid == null) {
			// If the token doesn't exist we create a new one
			gsid = new Cookie(SESSION_COOKIE_NAME, UUID.randomUUID().toString());
			gsid.setMaxAge(maxAge);
		}

		if (!sessionStore.hasSession(gsid.getId())) {
			sessionStore.setToken(gsid.getId(), gsid);
			gsid.setMaxAge(maxAge);
			gsid.setPath("/");
		} else {
			SessionToken existing = sessionStore.getToken(gsid.getValue());
			if (existing.isExpired()) {
				sessionStore.invalidateSession(existing.getId());
				gsid = new Cookie(SESSION_COOKIE_NAME, UUID.randomUUID().toString());
				gsid.setMaxAge(maxAge);
				sessionStore.setToken(gsid.getId(), gsid);
			}
		}

		response.setCookie(gsid);
		return sessionStore.get(gsid.getId());
	}
}
