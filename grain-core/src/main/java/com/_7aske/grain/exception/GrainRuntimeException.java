package com._7aske.grain.exception;

public class GrainRuntimeException extends RuntimeException {
	public GrainRuntimeException() {
	}
	public GrainRuntimeException(Throwable cause) {
		super(cause);
	}
	public GrainRuntimeException(String message) {
		super(message);
	}
	public GrainRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
