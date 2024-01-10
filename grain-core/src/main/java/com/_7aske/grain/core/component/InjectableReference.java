package com._7aske.grain.core.component;

import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static com._7aske.grain.core.component.InjectableReference.ReferenceType.*;

public class InjectableReference {
	private final Class<?> type;
	private final String name;
	private final ReferenceType referenceType;
	private final boolean isCollection;
	private static final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();
	private final Class<?> provider;
	private final AnnotatedBy annotatedBy;
	private final boolean required;

	private InjectableReference(Class<?> type, String name, Class<?> provider, AnnotatedBy annotatedBy, boolean isCollection, boolean isRequired) {
        this.type = type;
		this.name = name;
		this.provider = provider;
        this.annotatedBy = annotatedBy;
		this.required = isRequired;
        this.isCollection = isCollection;

        if (annotatedBy != null) {
            this.referenceType = ANNOTATION;
        } else if (name != null) {
            this.referenceType = NAME;
        } else {
            this.referenceType = TYPE;
        }
    }

	public static InjectableReference of(Parameter parameter) {
		String name = grainNameResolver.resolveReferenceName(parameter);
		boolean isCollection = false;
		Class<?> actualType = parameter.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(parameter);
			isCollection = true;
		}
		return new InjectableReference(
				actualType,
				name,
				parameter.getDeclaringExecutable().getDeclaringClass(),
				parameter.getAnnotation(AnnotatedBy.class),
                isCollection,
				true
		);
	}

	public static InjectableReference of(Method method) {
		String name = grainNameResolver.resolveReferenceName(method);
		boolean isCollection = false;
		Class<?> actualType = method.getReturnType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(method);
			isCollection = true;
		}
		return new InjectableReference(
				actualType,
				name,
				method.getDeclaringClass(),
				null,
                isCollection,
				true
		);
	}

	public static InjectableReference of(Field field) {
		String name = grainNameResolver.resolveReferenceName(field);

		boolean isCollection = false;
		Class<?> actualType = field.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(field);
			isCollection = true;
		}

		boolean isRequired = true;
		if (field.isAnnotationPresent(Inject.class)) {
			isRequired = field.getAnnotation(Inject.class).required();
		}

		return new InjectableReference(
				actualType,
				name,
				field.getDeclaringClass(),
				field.getAnnotation(AnnotatedBy.class),
                isCollection,
				isRequired
		);
	}

	Collection<Injectable> resolveList(DependencyContainerImpl container) {
		return switch (referenceType) {
			case TYPE -> container.getListByClass(type)
					.stream()
					.filter(d -> !Objects.equals(d.getType(), provider))
					.toList();
			case NAME -> container.getListByName(name);
			case ANNOTATION -> container.getListAnnotatedByClass(annotatedBy.value());
		};
	}

	Injectable resolve(DependencyContainerImpl container) {
		Optional<Injectable> optionalInjectable = switch (referenceType) {
			case TYPE -> container.getByClass(type);
			case NAME -> container.getByName(name).or(() -> container.getByClass(type));
			case ANNOTATION -> container.getByAnnotation(annotatedBy.value());
		};

		if (optionalInjectable.isEmpty() && required) {
			throw new GrainInitializationException("Dependency not found: " + this);
		}

		return optionalInjectable.orElse(null);
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return switch (referenceType) {
			case TYPE, ANNOTATION -> grainNameResolver.resolveReferenceName(type);
			case NAME -> name;
        };
	}

	public boolean isCollection() {
		return isCollection;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", type.getName(), name);
	}

	public enum ReferenceType {
		TYPE,
		NAME,
		ANNOTATION
	}
}
