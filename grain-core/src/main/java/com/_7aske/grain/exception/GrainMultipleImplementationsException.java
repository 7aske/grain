package com._7aske.grain.exception;

import com._7aske.grain.util.formatter.StringFormat;

public class GrainMultipleImplementationsException extends GrainRuntimeException {
	public GrainMultipleImplementationsException(Class<?> clazz) {
		super(StringFormat.format("Multiple implementations found for type {}. Use @Primary to mark the default implementation.", clazz.getName()));
	}
}
