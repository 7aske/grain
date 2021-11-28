package com._7aske.grain.security.exception;

public class CredentialsExpiredException extends GrainSecurityException {
	public CredentialsExpiredException() {
	}

	public CredentialsExpiredException(Throwable cause) {
		super(cause);
	}

	public CredentialsExpiredException(String message) {
		super(message);
	}

	public CredentialsExpiredException(String message, Throwable cause) {
		super(message, cause);
	}
}
