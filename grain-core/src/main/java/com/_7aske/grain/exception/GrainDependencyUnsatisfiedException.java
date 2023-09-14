package com._7aske.grain.exception;

import java.util.Arrays;

public class GrainDependencyUnsatisfiedException extends GrainInitializationException {
	public GrainDependencyUnsatisfiedException(Class<?> clazz, Class<?>[] params) {
		this(String.format("Grain dependencies unsatisfied for %s. Expected dependencies: %s", clazz, Arrays.toString(params)));
	}
	public GrainDependencyUnsatisfiedException(String message) {
		super(message);
	}

	public GrainDependencyUnsatisfiedException(String message, Throwable cause) {
		super(message, cause);
	}
}
