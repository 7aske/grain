package com._7aske.grain.security.exception;

public class UserDisabledException extends GrainSecurityException {
	public UserDisabledException() {
	}

	public UserDisabledException(Throwable cause) {
		super(cause);
	}

	public UserDisabledException(String message) {
		super(message);
	}

	public UserDisabledException(String message, Throwable cause) {
		super(message, cause);
	}
}
