package com._7aske.grain.gtl.interpreter.exception;

public class InterpreterNoSuchMethodException extends InterpreterException {
	public InterpreterNoSuchMethodException(Throwable e) {
		super(e);
	}

	public InterpreterNoSuchMethodException(String message) {
		super(message);
	}
}
