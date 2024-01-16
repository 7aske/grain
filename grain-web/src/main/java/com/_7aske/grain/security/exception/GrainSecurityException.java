package com._7aske.grain.security.exception;

import com._7aske.grain.exception.GrainRuntimeException;

// @Refactor Possibly extend HttpException to avoid rethrowing
public class GrainSecurityException extends GrainRuntimeException {
	public GrainSecurityException() {
	}

	public GrainSecurityException(Throwable cause) {
		super(cause);
	}

	public GrainSecurityException(String message) {
		super(message);
	}

	public GrainSecurityException(String message, Throwable cause) {
		super(message, cause);
	}
}
