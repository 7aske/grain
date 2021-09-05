package com._7aske.grain.exception;

public class GrainInitializationException extends RuntimeException {
	public GrainInitializationException(String message) {
		super(message);
	}

	public GrainInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
