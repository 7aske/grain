package com._7aske.grain.core.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class BetterDependencyField {
	private final Field field;
	private boolean initialized;

	public BetterDependencyField(Field field) {
		this.field = field;
		this.initialized = false;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return field.getAnnotation(annotationClass);
	}

	public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
		return field.isAnnotationPresent(annotationClass);
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Field get() {
		return field;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
