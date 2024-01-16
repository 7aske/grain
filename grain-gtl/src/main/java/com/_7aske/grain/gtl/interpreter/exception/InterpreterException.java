package com._7aske.grain.gtl.interpreter.exception;

public class InterpreterException extends RuntimeException {
	public InterpreterException(Throwable cause) {
		super(cause);
	}

	public InterpreterException(String message) {
		super(message);
	}
}
