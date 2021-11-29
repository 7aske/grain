package com._7aske.grain.http.session.tokenprovider;

import com._7aske.grain.component.Grain;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.session.SessionToken;

@Grain
public class HttpRequestTokenProvider {
	private final HttpRequestSessionTokenProviderStrategy strategy = new CookieHttpRequestSessionTokenProviderStrategy();

	public SessionToken getSessionToken(HttpRequest request) {
		return strategy.getSessionToken(request);
	}
}
