package com._7aske.grain.http.session;

public class SessionStoreDelegate implements Session {
	private final String sessionId;
	private final SessionStore sessionStore;

	public SessionStoreDelegate(String sessionId, SessionStore sessionStore) {
		this.sessionId = sessionId;
		this.sessionStore = sessionStore;
	}

	@Override
	public SessionToken getToken() {
		return sessionStore.getToken(sessionId);
	}

	@Override
	public String getId() {
		return sessionId;
	}

	public Object get(Object key) {
		return sessionStore.get(sessionId, key);
	}

	public void put(Object key, Object value) {
		sessionStore.put(sessionId, key, value);
	}

	public void remove(Object key) {
		sessionStore.remove(sessionId, key);
	}

	public boolean containsKey(Object key) {
		return sessionStore.containsKey(sessionId, key);
	}
}
