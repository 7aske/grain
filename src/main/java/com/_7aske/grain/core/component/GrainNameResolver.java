package com._7aske.grain.core.component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface GrainNameResolver {

	default String resolveDeclarationName(Class<?> clazz) {
		Grain grain = clazz.getAnnotation(Grain.class);
		if (grain != null && grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}
		return clazz.getName();
	}

	default String resolveReferenceName(Field field) {
		Inject inject = field.getAnnotation(Inject.class);
		if (inject != null && inject.name() != null && !inject.name().isEmpty()) {
			return inject.name();
		}

		return resolveDeclarationName(field.getType());
	}

	default String resolveReferenceName(Parameter parameter) {
		Inject inject = parameter.getAnnotation(Inject.class);
		if (inject != null && inject.name() != null && !inject.name().isEmpty()) {
			return inject.name();
		}

		return resolveDeclarationName(parameter.getType());
	}


	default String resolveDeclarationName(Method m) {
		Grain grain = m.getAnnotation(Grain.class);
		if (grain.name() != null && !grain.name().isEmpty()) {
			return grain.name();
		}

		return resolveDeclarationName(m.getReturnType());
	}

	static GrainNameResolver getDefault() {
		return new GrainNameResolver() {
		};
	}

}
