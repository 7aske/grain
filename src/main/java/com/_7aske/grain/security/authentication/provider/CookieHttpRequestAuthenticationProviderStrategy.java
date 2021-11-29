package com._7aske.grain.security.authentication.provider;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.session.Cookie;
import com._7aske.grain.http.session.SessionStore;
import com._7aske.grain.http.session.SessionToken;
import com._7aske.grain.http.session.tokenprovider.HttpRequestTokenProvider;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.CookieAuthentication;

@Grain
public class CookieHttpRequestAuthenticationProviderStrategy implements HttpRequestAuthenticationProviderStrategy {
	@Inject
	private SessionStore store;

	@Override
	public Authentication getAuthentication(HttpRequest request) {
		HttpRequestTokenProvider provider = ApplicationContextHolder.getContext().getGrain(HttpRequestTokenProvider.class);
		SessionToken cookie = provider.getSessionToken(request);
		if (cookie == null) return null;

		if (!store.hasSession(cookie.getId())) return null;

		SessionToken gsid = store.getToken(cookie.getId());
		if (gsid.isExpired()) return null;

		return new CookieAuthentication((Cookie) gsid);
	}
}
