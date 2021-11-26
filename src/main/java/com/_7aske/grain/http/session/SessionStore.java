package com._7aske.grain.http.session;

import com._7aske.grain.component.Grain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Grain
public class SessionStore {
	private final ConcurrentMap<String, Object> sessions;

	public SessionStore() {
		this.sessions = new ConcurrentHashMap<>();
	}
}
