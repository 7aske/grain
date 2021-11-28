package com._7aske.grain.security.exception;

public class InvalidCredentialsException extends GrainSecurityException {
	public InvalidCredentialsException() {
	}

	public InvalidCredentialsException(Throwable cause) {
		super(cause);
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}
}
