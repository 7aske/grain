package com._7aske.grain.http.session;

/**
 * This interface generalizes the operations for per session and
 * default operations that the user can call on the session store.
 */
public interface SessionStore {
	void setToken(String sessId, SessionToken sessionToken);
	SessionToken getToken(String sessId);
	Session get(String sessId);
	Object get(String sessId, Object key);
	void put(String sessId, Object key, Object value);
	void remove(String sessId);
	void remove(String sessId, Object key);
	boolean hasSession(String sessId);
	void initSession(String sessId);
	void invalidateSession(String sessId);
	boolean containsKey(String sessId, Object key);
}
