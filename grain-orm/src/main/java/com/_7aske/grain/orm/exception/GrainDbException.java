package com._7aske.grain.orm.exception;

import com._7aske.grain.exception.GrainRuntimeException;

public abstract class GrainDbException extends GrainRuntimeException {
	protected GrainDbException() {
	}

	public GrainDbException(String message) {
		super(message);
	}

	public GrainDbException(String message, Throwable cause) {
		super(message, cause);
	}

	public GrainDbException(Throwable cause) {
		super(cause);
	}
}
