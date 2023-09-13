package com._7aske.grain.http.session;

import com._7aske.grain.core.component.Grain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default store implementation. Possible implementations are
 * DatabaseSessionStore for example.
 */
@Grain
public class InMemorySessionStore implements SessionStore {
	private final ConcurrentMap<String, Map<Object, Object>> sessions;

	public InMemorySessionStore() {
		this.sessions = new ConcurrentHashMap<>();
	}

	@Override
	public void setToken(String sessId, SessionToken sessionToken) {
		if (!sessions.containsKey(sessId)){
			initSession(sessId);
		}
		// @Refactor
		sessions.get(sessId).put("__TOKEN", sessionToken);
	}

	@Override
	public SessionToken getToken(String sessId) {
		return (SessionToken) sessions.get(sessId).get("__TOKEN");
	}

	@Override
	public Session get(String sessId) {
		return new SessionStoreDelegate(sessId, this);
	}

	@Override
	public Object get(String sessId, Object key) {
		return sessions.get(sessId).get(key);
	}

	@Override
	public void put(String sessId, Object key, Object value) {
		sessions.get(sessId).put(key, value);
	}

	@Override
	public void remove(String sessId) {
		sessions.remove(sessId);
	}

	@Override
	public void remove(String sessId, Object key) {
		sessions.get(sessId).remove(key);
	}

	@Override
	public boolean hasSession(String sessId) {
		return sessions.containsKey(sessId);
	}

	@Override
	public void initSession(String sessId) {
		sessions.put(sessId, new HashMap<>());
	}

	@Override
	public void invalidateSession(String sessId) {
		sessions.remove(sessId);
	}

	@Override
	public boolean containsKey(String sessId, Object key) {
		return sessions.get(sessId).containsKey(key);
	}
}
