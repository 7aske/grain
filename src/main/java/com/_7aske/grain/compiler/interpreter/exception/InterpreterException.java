package com._7aske.grain.compiler.interpreter.exception;

import com._7aske.grain.exception.GrainRuntimeException;

public class InterpreterException extends GrainRuntimeException {
	public InterpreterException(Throwable cause) {
		super(cause);
	}

	public InterpreterException(String message) {
		super(message);
	}
}
