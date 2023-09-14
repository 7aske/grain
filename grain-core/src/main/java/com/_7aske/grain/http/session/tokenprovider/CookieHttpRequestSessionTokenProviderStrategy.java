package com._7aske.grain.http.session.tokenprovider;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.session.SessionConstants;
import com._7aske.grain.http.session.SessionToken;

@Grain
public class CookieHttpRequestSessionTokenProviderStrategy implements HttpRequestSessionTokenProviderStrategy {
	@Override
	public SessionToken getSessionToken(HttpRequest request) {
		return request.getCookie(SessionConstants.SESSION_COOKIE_NAME);
	}
}
