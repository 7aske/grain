package com._7aske.grain.http.session;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

import java.util.Objects;
import java.util.UUID;

import static com._7aske.grain.config.Configuration.Key;
import static com._7aske.grain.http.session.SessionConstants.SESSION_COOKIE_NAME;
import static com._7aske.grain.http.session.SessionConstants.SESSION_DEFAULT_MAX_AGE;

@Grain
public class CookieSessionInitializer implements SessionInitializer {
	@Inject
	private Configuration configuration;
	@Inject
	private SessionStore sessionStore;

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
