package com._7aske.grain.core.component;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface GrainNameResolver {

	default @Nullable String resolveDeclarationName(Class<?> clazz) {
		Grain grain = clazz.getAnnotation(Grain.class);
		if (grain != null && grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}
		return null;
	}

	default @NotNull String resolveReferenceName(Class<?> clazz) {
		Grain grain = clazz.getAnnotation(Grain.class);
		if (grain != null && grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}
		String className = clazz.getSimpleName();
		if (className.endsWith("[]")) {
			className = className.substring(className.length() - 2);
		}
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}

	default @Nullable String resolveReferenceName(Field field) {
		Inject inject = field.getAnnotation(Inject.class);
		if (inject != null && inject.name() != null && !inject.name().isEmpty()) {
			return inject.name();
		}

		return null;
	}

	default @Nullable String resolveReferenceName(Parameter parameter) {
		Inject inject = parameter.getAnnotation(Inject.class);
		if (inject != null && inject.name() != null && !inject.name().isEmpty()) {
			return inject.name();
		}

		return null;
	}


	default @Nullable String resolveReferenceName(Method m) {
		Grain grain = m.getAnnotation(Grain.class);
		if (grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}

		return m.getName();
	}

	static GrainNameResolver getDefault() {
		return new GrainNameResolver() {
		};
	}

}
