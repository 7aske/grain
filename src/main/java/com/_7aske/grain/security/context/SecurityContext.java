package com._7aske.grain.security.context;

import com._7aske.grain.security.Authentication;

/**
 * Interface representing current security context. It should be retrieved
 * from {@link SecurityContextHolder} as it is the only valid method of retrieving it.
 * SecurityContext contains current {@link Authentication} for the request.
 */
public interface SecurityContext {

	/**
	 * Gets current authentication. Null if the user is not authenticated.
	 * @return current authentication.
	 */
	Authentication getAuthentication();

	/**
	 * Sets current authentication for the context.
	 * @param authentication to be set.
	 */
	void setAuthentication(Authentication authentication);
}
