package com._7aske.grain.security.authentication.provider;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.security.Authentication;

/**
 * Interface providing the functionality of extracting Authentication from
 * the incoming {@link HttpRequest}.
 */
public interface HttpRequestAuthenticationProviderStrategy {

	/**
	 * @param request to extract authentication from.
	 * @return extracted Authentication. Null if unable to extract.
	 */
	Authentication getAuthentication(HttpRequest request);
}
