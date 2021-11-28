package com._7aske.grain.security;

import java.util.Collection;

/**
 * Interface representing user authentication in the security context.
 */
public interface Authentication {
	/**
	 * @return authentication user.
	 */
	String getName();

	/**
	 * @return authentication user credentials.
	 */
	Object getCredentials();

	/**
	 * @return returns true if user is successfully authenticated.
	 */
	boolean isAuthenticated();

	/**
	 * Sets user authentication status.
	 */
	void setAuthenticated(boolean authenticated);

	/**
	 * @return Authenticated user roles.
	 */
	Collection<? extends Authority> getAuthorities();

	/**
	 * Sets authentication authorities. Does not modify user's authorities.
	 *
	 * @param authorities authorities to be set to the authentication.
	 */
	void setAuthorities(Collection<? extends Authority> authorities);
}
