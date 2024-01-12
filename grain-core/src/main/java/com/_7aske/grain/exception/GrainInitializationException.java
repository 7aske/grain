package com._7aske.grain.exception;

public class GrainInitializationException extends GrainRuntimeException {
	public GrainInitializationException(String message) {
		super(message);
	}

	public GrainInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
