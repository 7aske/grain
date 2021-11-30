package com._7aske.grain.security.authentication.provider;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.session.Cookie;
import com._7aske.grain.http.session.SessionStore;
import com._7aske.grain.http.session.SessionToken;
import com._7aske.grain.http.session.tokenprovider.HttpRequestTokenProvider;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.CookieAuthentication;
import com._7aske.grain.security.SecurityConstants;

/**
 * Provides Authentication from a valid cookie based session
 */
@Grain
public class CookieHttpRequestAuthenticationProviderStrategy implements HttpRequestAuthenticationProviderStrategy {
	@Inject
	private SessionStore store;
	@Inject
	private HttpRequestTokenProvider provider;

	@Override
	public Authentication getAuthentication(HttpRequest request) {
		SessionToken cookie = provider.getSessionToken(request);
		if (cookie == null) return null;

		if (!store.hasSession(cookie.getId())) return null;

		SessionToken gsid = store.getToken(cookie.getId());
		if (gsid.isExpired()) return null;
		Authentication authentication = (Authentication) store.get(gsid.getId(), SecurityConstants.AUTHENTICATION_KEY);

		if (authentication == null || !authentication.isAuthenticated())
			return null;

		return new CookieAuthentication((Cookie) gsid);
	}
}
