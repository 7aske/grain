package com._7aske.grain.security.exception;

public class AccountLockedException extends GrainSecurityException {
	public AccountLockedException() {
	}

	public AccountLockedException(Throwable cause) {
		super(cause);
	}

	public AccountLockedException(String message) {
		super(message);
	}

	public AccountLockedException(String message, Throwable cause) {
		super(message, cause);
	}
}
