package com._7aske.grain.http.session;

public interface SessionToken {
	String getId();
	boolean isExpired();
}
