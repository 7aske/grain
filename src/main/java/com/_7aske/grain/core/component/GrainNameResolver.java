package com._7aske.grain.core.component;

import com._7aske.grain.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface GrainNameResolver {

	default @Nullable String resolveReferenceName(Class<?> clazz) {
		Grain grain = clazz.getAnnotation(Grain.class);
		if (grain != null && grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}
		return null;
	}

	default @Nullable String resolveReferenceName(Field field) {
		Inject inject = field.getAnnotation(Inject.class);
		if (inject != null && inject.name() != null && !inject.name().isEmpty()) {
			return inject.name();
		}

		return resolveReferenceName(field.getType());
	}

	default @Nullable String resolveReferenceName(Parameter parameter) {
		Inject inject = parameter.getAnnotation(Inject.class);
		if (inject != null && inject.name() != null && !inject.name().isEmpty()) {
			return inject.name();
		}

		return resolveReferenceName(parameter.getType());
	}


	default @Nullable String resolveReferenceName(Method m) {
		Grain grain = m.getAnnotation(Grain.class);
		if (grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}

		return resolveReferenceName(m.getReturnType());
	}

	static GrainNameResolver getDefault() {
		return new GrainNameResolver() {
		};
	}

}
