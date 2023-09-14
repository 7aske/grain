package com._7aske.grain.web.controller.exception;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.util.formatter.StringFormat;

public class NoValidConverterException extends GrainRuntimeException {
	public NoValidConverterException(Class<?> clazz) {
		super(StringFormat.format("No valid converter found for class {}", clazz));
	}
}
