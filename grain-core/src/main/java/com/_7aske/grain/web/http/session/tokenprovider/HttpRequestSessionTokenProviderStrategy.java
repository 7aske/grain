package com._7aske.grain.web.http.session.tokenprovider;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.session.SessionToken;

/**
 * Interface describing how the token is extracted from the HttpRequest.
 */
public interface HttpRequestSessionTokenProviderStrategy {

	/**
	 * @param request incoming request
	 * @return extracted {@link SessionToken}
	 */
	SessionToken getSessionToken(HttpRequest request);
}
