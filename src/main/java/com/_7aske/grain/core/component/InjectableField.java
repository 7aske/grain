package com._7aske.grain.core.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class InjectableField {
	private final Field field;

	public InjectableField(Field field) {
		this.field = field;
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
}
