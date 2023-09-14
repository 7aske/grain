package com._7aske.grain.http.session;

/**
 * Interface describing the unique session identification token. SessionToken
 * is used to uniquely identify a session in the session store.
 */
public interface SessionToken {
	/**
	 * @return unique identification of the token.
	 */
	String getId();

	/**
	 * @return whether the session token is still valid for use.
	 */
	boolean isExpired();
}
