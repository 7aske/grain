package com._7aske.grain.web.http.session.tokenprovider;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.session.SessionToken;

@Grain
public class HttpRequestTokenProvider {
	private final HttpRequestSessionTokenProviderStrategy strategy = new CookieHttpRequestSessionTokenProviderStrategy();

	public SessionToken getSessionToken(HttpRequest request) {
		return strategy.getSessionToken(request);
	}
}
