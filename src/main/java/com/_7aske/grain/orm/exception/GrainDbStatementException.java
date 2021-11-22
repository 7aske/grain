package com._7aske.grain.orm.exception;

public class GrainDbStatementException extends GrainDbException {
	public GrainDbStatementException() {
	}

	public GrainDbStatementException(String message) {
		super(message);
	}

	public GrainDbStatementException(String message, Throwable cause) {
		super(message, cause);
	}

	public GrainDbStatementException(Throwable cause) {
		super(cause);
	}
}
