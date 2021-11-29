package com._7aske.grain.util;

import com._7aske.grain.GrainApp;
import com._7aske.grain.component.Default;
import com._7aske.grain.component.Primary;
import com._7aske.grain.exception.GrainMultipleImplementationsException;
import com._7aske.grain.exception.GrainReflectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReflectionUtil {
	private ReflectionUtil() {
	}

	// RuntimeException version of getAnyConstructor
	public static <T> Constructor<T> getAnyConstructorNoThrow(Class<T> clazz) {
		try {
			return getAnyConstructor(clazz);
		} catch (NoSuchMethodException e) {
			throw new GrainReflectionException(e);
		}
	}

	public static <T> Constructor<T> getAnyConstructor(Class<T> clazz) throws NoSuchMethodException {
		Constructor<T> constructor = null;
		NoSuchMethodException cause = new NoSuchMethodException();
		try {
			constructor = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			cause = e;
		}

		if (constructor == null) {
			try {
				constructor = clazz.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
				cause = e;
			}
		}

		if (constructor == null) {
			constructor = (Constructor<T>) clazz.getEnclosingConstructor();
		}

		if (constructor != null) {
			constructor.setAccessible(true);
			return constructor;
		}

		throw cause;
	}

	public static boolean haveCommonInterfaces(Class<?> clazz1, Class<?> clazz2) {
		List<Class<?>> interfaces1 = Arrays.asList(clazz1.getInterfaces());
		List<Class<?>> interfaces2 = Arrays.asList(clazz2.getInterfaces());
		if (clazz2.isInterface()) return interfaces1.contains(clazz2);
		if (clazz1.isInterface()) return interfaces2.contains(clazz1);
		return interfaces1.stream().anyMatch(interfaces2::contains);
	}

	public static <T> Constructor<T> getBestConstructor(Class<T> clazz) throws NoSuchMethodException {
		try {
			return getAnyConstructor(clazz);
		} catch (NoSuchMethodException ignored) {
		}
		Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
		if (constructors.length == 0)
			constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
		for (Constructor<T> c : constructors)
			c.setAccessible(true);
		if (constructors.length == 0)
			throw new NoSuchMethodException();
		if (constructors.length == 1)
			return constructors[0];
		Arrays.sort(constructors, (Comparator.comparingInt(Constructor::getParameterCount)));
		return constructors[constructors.length - 1];
	}


	public static Class<?> loadClass(String className, String packageName) {
		try {
			String fullClassName = packageName + "." + className.substring(0, className.lastIndexOf('.'));
			return Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotation) {
		if (clazz.isAnnotationPresent(annotation)) return true;
		// check if annotation is inherited
		return Arrays.stream(clazz.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(annotation));
	}

	public static boolean isAnnotationPresent(Field field, Class<? extends Annotation> annotation) {
		return field.isAnnotationPresent(annotation);
	}

	public static <T> T newInstance(Class<T> clazz) throws GrainReflectionException {
		try {
			return getAnyConstructor(clazz).newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new GrainReflectionException(e);
		}
	}

	public static <T> Class<T> getGenericListTypeArgument(Field f) {
		return (Class<T>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
	}


	public static <T> Optional<T> findClassByClass(Class<?> clazz, Collection<T> classes, Function<T, Class<?>> extractor) {
		List<T> result = classes.stream()
				.filter(d -> extractor.apply(d).equals(clazz) || clazz.isAssignableFrom(extractor.apply(d)))
				.collect(Collectors.toList());

		if (result.size() > 1) {
			// User defined dependencies are the ones that do not start
			// with grain library base package which is the package of
			// GrainApp.class.
			List<T> userDefined = result.stream()
					.filter(dep -> {
						String basePackage = GrainApp.class.getPackageName();
						String depPackage = extractor.apply(dep).getPackageName();

						// If the package is not starting with package but if it is make sure by checking whether the next
						// letter after the basePackage is a dot since in case of com._7aske.grain as basePackge and
						// com._7aske.graintest only by checking starts with would return true. This can be refactored
						// to match paths like we do it for url path matching.
						return !(depPackage.startsWith(basePackage) &&
								depPackage.charAt(basePackage.length()) == '.');
					})
					.collect(Collectors.toList());
			if (userDefined.size() > 1) {
				if (userDefined.stream().noneMatch(g -> isAnnotationPresent(g.getClass(), Primary.class))) {
					throw new GrainMultipleImplementationsException(clazz);
				} else {
					// @Incomplete Handle the case where use has defined
					// multiple @Primary grains
					return userDefined.stream()
							.filter(g -> isAnnotationPresent(g.getClass(), Primary.class))
							.findFirst();
				}
			} else if (userDefined.size() == 1) {
				return Optional.of(userDefined.get(0));
			} else {
				// We have multiple library defined dependencies
				return result.stream().filter(c -> !c.getClass().isAnnotationPresent(Default.class)).findFirst();
			}
		} else if (result.isEmpty()) {
			return Optional.empty();
		} else {
			// in any other case just return the first found dependency
			return Optional.of(result.get(0));
		}
	}
}
