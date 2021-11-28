package com._7aske.grain.security;

import java.util.Collection;

/**
 * Interface representing user object used in authentication lifecycle.
 */
public interface User {

	/**
	 * Gets the user's name identification. Typically, username.
	 * @return user's name
	 */
	String getUsername();

	/**
	 * Gets the user's credentials. Typically, password.
	 * @return user's password.
	 */
	Object getPassword();


	/**
	 * Gets the user's authorities (roles).
	 * @return user's authorities.
	 */
	Collection<? extends Authority> getAuthorities();

	/**
	 * @return user account locked status.
	 */
	boolean isAccountLocked();

	/**
	 * @return user credentials expired status.
	 */
	boolean isCredentialsExpired();

	/**
	 * @return user account enabled status.
	 */
	boolean isEnabled();
}
