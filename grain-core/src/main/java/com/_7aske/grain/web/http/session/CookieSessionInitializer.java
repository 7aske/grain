package com._7aske.grain.web.http.session;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static com._7aske.grain.web.http.session.SessionConstants.SESSION_COOKIE_NAME;
import static com._7aske.grain.web.http.session.SessionConstants.SESSION_DEFAULT_MAX_AGE;

@Grain
public class CookieSessionInitializer implements SessionInitializer {
	@Inject
	private Configuration configuration;
	@Inject
	private SessionStore sessionStore;

	public Session initialize(HttpRequest request, HttpResponse response) {
		if (!Objects.equals(configuration.getBoolean(ConfigurationKey.SESSION_ENABLED), true)) {
			return null;
		}

		long maxAge = System.currentTimeMillis() + configuration.getLong(ConfigurationKey.SESSION_MAX_AGE, SESSION_DEFAULT_MAX_AGE);
		Cookie gsid = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(SESSION_COOKIE_NAME))
				.findFirst()
				.orElseGet(() -> {
					// If the token doesn't exist we create a new one
					Cookie cookie = new Cookie(SESSION_COOKIE_NAME, UUID.randomUUID().toString());
					cookie.setMaxAge(maxAge);
					return cookie;
				});

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

		// @Hack
		if (response instanceof GrainHttpResponse res) {
			res.setCookie(gsid);
		}

		return sessionStore.get(gsid.getId());
	}
}
