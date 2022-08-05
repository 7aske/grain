package com._7aske.grain.core.component;

import com._7aske.grain.exception.GrainInitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com._7aske.grain.core.component.DependencyReference.ReferenceType.NAME;
import static com._7aske.grain.core.component.DependencyReference.ReferenceType.TYPE;

public class DependencyReference {
	private final Class<?> type;
	private final String name;
	private final ReferenceType referenceType;
	private static final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();

	public DependencyReference(Class<?> type, String name, ReferenceType referenceType) {
		this.type = type;
		this.name = name;
		this.referenceType = referenceType;
	}

	public static DependencyReference byType(Class<?> type) {
		return new DependencyReference(type, null, TYPE);
	}

	public static DependencyReference byName(Class<?> type, String name) {
		return new DependencyReference(type, name, NAME);
	}

	public static DependencyReference of(Parameter parameter) {
		String name = grainNameResolver.resolveReferenceName(parameter);
		return new DependencyReference(parameter.getType(), name, name == null ? TYPE : NAME);
	}

	public static DependencyReference of(Class<?> type) {
		String name = grainNameResolver.resolveReferenceName(type);
		return new DependencyReference(type, name, name == null ? TYPE : NAME);
	}

	public static DependencyReference of(Method method) {
		String name = grainNameResolver.resolveReferenceName(method);
		return new DependencyReference(method.getReturnType(), name, name == null ? TYPE : NAME);
	}

	public static DependencyReference of(Field field) {
		String name = grainNameResolver.resolveReferenceName(field);
		return new DependencyReference(field.getType(), name, name == null ? TYPE : NAME);
	}

	public BetterDependency resolve(DependencyContainer container) {
		if (referenceType == TYPE) {
			return container.getByClass(type)
					.orElseThrow(() -> new GrainInitializationException("No dependency of type '" + type + "'"));
		}

		if (referenceType == NAME) {
			return container.getByName(name)
					.or(() -> container.getByClass(type))
					.orElseThrow(() -> new GrainInitializationException("No dependency with name '" + name + "'"));
		}

		throw new GrainInitializationException("Unknown reference type");
	}

	public String getName() {
		if (referenceType == NAME) {
			return name;
		}

		if (referenceType == TYPE) {
			return type.getName();
		}

		return null;
	}

	public enum ReferenceType {
		TYPE,
		NAME
	}
}
