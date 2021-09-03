package com._7aske.grain.exception;

public class GrainRuntimeException extends RuntimeException {
	public GrainRuntimeException(String message) {
		super(message);
	}
	public GrainRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
