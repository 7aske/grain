package com._7aske.grain.security;

/**
 * Interface representing a role in the authentication pipeline.
 */
public interface Authority {

	/**
	 * Gets the string identifier of the authority.
	 * @return authorization name.
	 */
	String getName();
}
