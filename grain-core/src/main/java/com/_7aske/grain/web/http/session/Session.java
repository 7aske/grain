package com._7aske.grain.web.http.session;

/**
 * This interface generalizes the operations for per session and
 * default operations that the user can call on the session store.
 */
public interface Session {
	/**
	 * Gets the SessionToken object that identifies the current session
	 * @return SessionToken object
	 */
	SessionToken getToken();

	/**
	 * Gets the ID of the current session
	 * @return String id
	 */
	String getId();

	/**
	 * Gets any object from the session store corresponding to this sessionId
	 * @param key
	 * @return
	 */
	Object get(Object key);

	/**
	 * Sets any object to the session store corresponding to this sessionId
	 * @param key
	 * @param value
	 */
	void put(Object key, Object value);

	/**
	 * Removes the object from store corresponding to this sessionId
	 * @param key
	 */
	void remove(Object key);

	/**
	 * Checks whether session store corresponding to this sessionId contains
	 * provided key
	 * @param key
	 */
	boolean containsKey(Object key);

	/**
	 * Invalidates the session
	 */
	void invalidate();
}
