package com._7aske.grain.core.component;

import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import static com._7aske.grain.core.component.DependencyReference.ReferenceType.NAME;
import static com._7aske.grain.core.component.DependencyReference.ReferenceType.TYPE;

public class DependencyReference {
	private final Class<?> type;
	private final String name;
	private final ReferenceType referenceType;
	private final boolean isCollection;
	private static final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();

	public DependencyReference(Class<?> type, String name, ReferenceType referenceType, boolean isCollection) {
		this.type = type;
		this.name = name;
		this.referenceType = referenceType;
		this.isCollection = isCollection;
	}

	public static DependencyReference of(Parameter parameter) {
		String name = grainNameResolver.resolveReferenceName(parameter);
		boolean isCollection = false;
		Class<?> actualType = parameter.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(parameter);
			isCollection = true;
		}
		return new DependencyReference(actualType, name, name == null ? TYPE : NAME, isCollection);
	}

	public static DependencyReference of(Method method) {
		String name = grainNameResolver.resolveReferenceName(method);
		boolean isCollection = false;
		Class<?> actualType = method.getReturnType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(method);
			isCollection = true;
		}
		return new DependencyReference(actualType, name, name == null ? TYPE : NAME, isCollection);
	}

	public static DependencyReference of(Field field) {
		String name = grainNameResolver.resolveReferenceName(field);
		boolean isCollection = false;
		Class<?> actualType = field.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(field);
			isCollection = true;
		}
		return new DependencyReference(actualType, name, name == null ? TYPE : NAME, isCollection);
	}

	public Collection<Injectable> resolveList(DependencyContainerImpl container) {
		if (referenceType == TYPE) {
			return container.getListByClass(type);
		}

		if (referenceType == NAME) {
			return container.getListByName(name);
		}

		throw new GrainInitializationException("Unknown reference type");
	}

	public Injectable resolve(DependencyContainerImpl container) {
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

	public boolean isCollection() {
		return isCollection;
	}

	public enum ReferenceType {
		TYPE,
		NAME
	}
}
