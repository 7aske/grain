package com._7aske.grain.security.authentication.provider;

import com._7aske.grain.core.component.ConditionalOnMissingGrain;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.SecurityConstants;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.session.SessionStore;
import com._7aske.grain.web.http.session.SessionToken;
import com._7aske.grain.web.http.session.tokenprovider.HttpRequestTokenProvider;

/**
 * Provides Authentication from a valid cookie based session
 */
@Grain
@ConditionalOnMissingGrain
public class CookieHttpRequestAuthenticationProviderStrategy implements HttpRequestAuthenticationProviderStrategy {
	private final SessionStore store;
	private final HttpRequestTokenProvider provider;

    public CookieHttpRequestAuthenticationProviderStrategy(SessionStore store, HttpRequestTokenProvider provider) {
        this.store = store;
        this.provider = provider;
    }

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

		return authentication;
	}
}
