package com._7aske.grain.exception;

public class GrainReflectionException extends RuntimeException {
	public GrainReflectionException() {
	}

	public GrainReflectionException(String message) {
		super(message);
	}

	public GrainReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public GrainReflectionException(Throwable cause) {
		super(cause);
	}
}
