package com._7aske.grain.security.exception;

import com._7aske.grain.exception.GrainRuntimeException;

public class GrainSecurityNullContextException extends GrainRuntimeException {
	public GrainSecurityNullContextException() {
		super("Security context cannot be null");
	}
}
