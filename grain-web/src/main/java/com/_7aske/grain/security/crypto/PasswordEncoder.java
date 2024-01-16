package com._7aske.grain.security.crypto;

public interface PasswordEncoder {
	String encode(String password);
	// @Refactor this should be a char[] for security
	boolean matches(String password, String hashed);
}
