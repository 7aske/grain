package com._7aske.grain.web.http.session.tokenprovider;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.session.Cookie;
import com._7aske.grain.web.http.session.SessionConstants;
import com._7aske.grain.web.http.session.SessionToken;

import java.util.Arrays;
import java.util.Objects;

@Grain
public class CookieHttpRequestSessionTokenProviderStrategy implements HttpRequestSessionTokenProviderStrategy {
	@Override
	public SessionToken getSessionToken(HttpRequest request) {
		return Arrays.stream(request.getCookies())
				.filter(cookie -> Objects.equals(cookie.getName(), SessionConstants.SESSION_COOKIE_NAME))
				.findFirst()
				.orElse(null);
	}
}
