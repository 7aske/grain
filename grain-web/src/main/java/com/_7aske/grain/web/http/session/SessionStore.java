package com._7aske.grain.web.http.session;

/**
 * This interface generalizes the operations for per session and
 * default operations that the user can call on the session store.
 * Session store is a persistent storage for all session data.
 */
public interface SessionStore {
	/**
	 * Sets the token with the given id to the session store.
	 *
	 * @param sessId       unique identification for the session
	 * @param sessionToken valid SessionToken object. By default a cookie.
	 */
	void setToken(String sessId, SessionToken sessionToken);

	/**
	 * @param sessId identification of session for which we're fetching the token.
	 * @return token for the session identified by sessId
	 */
	SessionToken getToken(String sessId);

	/**
	 * @param sessId unique identification for the session we're fetching.
	 * @return Session implementation corresponding to the provided sessId
	 * identification.
	 */
	Session get(String sessId);

	/**
	 * @param sessId unique identification for the session data we're fetching.
	 * @param key of the object we're fetching.
	 * @return value of the key-pair identified by key.
	 */
	Object get(String sessId, Object key);

	/**
	 * Updates the session store session identified by sessId with the key-value
	 * pair specified by param key and param value.
	 * @param sessId unique identification for the session that we want to update.
	 * @param key of the object we're updating.
	 * @param value of the key-pair identified by key.
	 */
	void put(String sessId, Object key, Object value);

	/**
	 * Removes the session identified by sessId from the session store.
	 * @param sessId unique identification.
	 */
	void remove(String sessId);

	/**
	 * Removes the object identified by key from session identified by sessId.
	 * @param sessId unique identification.
	 * @param key property to remove from session store.
	 */
	void remove(String sessId, Object key);

	/**
	 * @param sessId identification for the session that we want to check the
	 * existence of.
	 * @return true if the session is present.
	 */
	boolean hasSession(String sessId);

	/**
	 * Initializes the session with a given session id
	 * @param sessId identification of the session we want to initialize.
	 */
	void initSession(String sessId);

	/**
	 * Invalidates the session with the given sessId.
	 * @param sessId identification of the session we want to invalidate.
	 */
	void invalidateSession(String sessId);

	/**
	 * Checks whether the session has a value identified by the key.
	 * @param sessId identification of the session we want check for the key.
	 * @param key identification the value we want to check the session store for
	 * @return true if the key is defined in the store regardless of its value.
	 */
	boolean containsKey(String sessId, Object key);
}
