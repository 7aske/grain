package com._7aske.grain.core.component;

import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.stream.Collectors;

import static com._7aske.grain.core.component.InjectableReference.ReferenceType.NAME;
import static com._7aske.grain.core.component.InjectableReference.ReferenceType.TYPE;

public class InjectableReference<T> {
	private final Class<T> type;
	private final String name;
	private final ReferenceType referenceType;
	private final boolean isCollection;
	private static final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();
	private final Class<?> provider;

	public InjectableReference(Class<T> type, String name, ReferenceType referenceType, boolean isCollection, Class<?> provider) {
		this.type = type;
		this.name = name;
		this.referenceType = referenceType;
		this.isCollection = isCollection;
		this.provider = provider;
	}

	public static InjectableReference<?> of(Parameter parameter) {
		String name = grainNameResolver.resolveReferenceName(parameter);
		boolean isCollection = false;
		Class<?> actualType = parameter.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(parameter);
			isCollection = true;
		}
		return new InjectableReference<>(actualType, name, name == null ? TYPE : NAME, isCollection, parameter.getDeclaringExecutable().getDeclaringClass());
	}

	public static InjectableReference<?> of(Method method) {
		String name = grainNameResolver.resolveReferenceName(method);
		boolean isCollection = false;
		Class<?> actualType = method.getReturnType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(method);
			isCollection = true;
		}
		return new InjectableReference<>(actualType, name, name == null ? TYPE : NAME, isCollection, method.getDeclaringClass());
	}

	public static InjectableReference<?> of(Field field) {
		String name = grainNameResolver.resolveReferenceName(field);
		boolean isCollection = false;
		Class<?> actualType = field.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(field);
			isCollection = true;
		}
		return new InjectableReference<>(actualType, name, name == null ? TYPE : NAME, isCollection, field.getDeclaringClass());
	}

	public Collection<Injectable<?>> resolveList(DependencyContainerImpl container) {
		if (referenceType == TYPE) {
			return container.getListByClass(type)
					.stream()
					.filter(d -> !d.getType().equals(provider))
					.collect(Collectors.toList());
		}

		if (referenceType == NAME) {
			return container.getListByName(name);
		}

		throw new GrainInitializationException("Unknown reference type");
	}

	public Injectable<T> resolve(DependencyContainerImpl container) {
		if (referenceType == TYPE) {
			return (Injectable<T>) container.getByClass(type)
					.orElseThrow(() -> new GrainInitializationException("No dependency of type '" + type + "'"));
		}

		if (referenceType == NAME) {
			return (Injectable<T>) container.getByName(name)
					.or(() -> container.getByClass(type))
					.orElseThrow(() -> new GrainInitializationException("No dependency with name '" + name + "'"));
		}

		throw new GrainInitializationException("Unknown reference type");
	}

	public Class<?> getType() {
		return type;
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
