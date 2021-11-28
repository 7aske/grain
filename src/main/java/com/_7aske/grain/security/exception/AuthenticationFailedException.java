package com._7aske.grain.security.exception;

public class AuthenticationFailedException extends GrainSecurityException {
	public AuthenticationFailedException() {
	}

	public AuthenticationFailedException(Throwable cause) {
		super(cause);
	}

	public AuthenticationFailedException(String message) {
		super(message);
	}

	public AuthenticationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
