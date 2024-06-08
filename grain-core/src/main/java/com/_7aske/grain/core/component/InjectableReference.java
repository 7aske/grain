package com._7aske.grain.core.component;

import com._7aske.grain.core.reflect.ReflectionUtil;
import com._7aske.grain.exception.GrainInitializationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

import static com._7aske.grain.core.component.InjectableReference.ReferenceType.*;

public class InjectableReference {
	private final Class<?> type;
	private final String name;
	private final ReferenceType referenceType;
	private final boolean isCollection;
	private static final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();
	private final Class<?> provider;
	private final Class<? extends Annotation>[] annotatedBy;
	private final boolean required;

	private InjectableReference(Class<?> type, String name, Class<?> provider, Class<? extends Annotation>[] annotatedBy, boolean isCollection, boolean isRequired) {
        this.type = type;
		this.name = name;
		this.provider = provider;
        this.annotatedBy = annotatedBy;
		this.required = isRequired;
        this.isCollection = isCollection;

        if (annotatedBy != null && annotatedBy.length > 0) {
            this.referenceType = ANNOTATION;
        } else if (name != null) {
            this.referenceType = NAME;
        } else {
            this.referenceType = TYPE;
        }

		if (referenceType == TYPE && type.equals(Object.class)) {
			throw new GrainInitializationException("Ambigious dependency definition for " + this + " in " + provider);
		}
    }

	public static InjectableReference of(Class<?> clazz) {
		return new InjectableReference(
				clazz,
				null,
				null,
				null,
				false,
				true);
	}

	public static InjectableReference of(Parameter parameter) {
		String name = grainNameResolver.resolveReferenceName(parameter);
		boolean isCollection = false;
		Class<?> actualType = parameter.getType();
		if (Collection.class.isAssignableFrom(actualType)) {
			actualType = ReflectionUtil.getGenericListTypeArgument(parameter);
			isCollection = true;
		}
		Optional<Inject> inject = Optional.ofNullable(parameter.getAnnotation(Inject.class));
		return new InjectableReference(
				actualType,
				name,
				parameter.getDeclaringExecutable().getDeclaringClass(),
				inject.map(Inject::annotatedBy)
						.orElse(null),
                isCollection,
				inject.map(Inject::required)
						.orElse(true)
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
				Optional.ofNullable(field.getAnnotation(Inject.class))
						.map(Inject::annotatedBy)
						.orElse(null),
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
			case ANNOTATION -> Stream.of(annotatedBy)
					.flatMap(a -> container.getListAnnotatedByClass(a).stream())
					.toList();
		};
	}

	public Injectable resolve(DependencyContainerImpl container) {
		Optional<Injectable> optionalInjectable = switch (referenceType) {
			case TYPE -> container.getByClass(type);
			case NAME -> container.getByName(name);
			case ANNOTATION -> {
				List<Optional<Injectable>> optional = Arrays.stream(annotatedBy)
						.map(container::getByAnnotation)
						.toList();
				if (optional.size() > 1) {
					throw new GrainInitializationException("Multiple dependencies found for " + this);
				}

				yield optional.get(0);
			}
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
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		InjectableReference that = (InjectableReference) object;
		return Objects.equals(type, that.type) && Objects.equals(name, that.name) && referenceType == that.referenceType && Objects.equals(provider, that.provider);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(type, name, referenceType, isCollection, provider, required);
		result = 31 * result + Arrays.hashCode(annotatedBy);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (isCollection) {
			builder.append("List<");
		}

		builder.append(type.getSimpleName());

		String grainName = Optional.ofNullable(name)
				.map("\"%s\""::formatted)
				.orElse("");
		builder.append("(").append(grainName).append(")");

		if (isCollection) {
			builder.append(">");
		}

		return builder.toString();
	}

	public enum ReferenceType {
		TYPE,
		NAME,
		ANNOTATION
	}
}
