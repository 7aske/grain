package com._7aske.grain.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public class ReflectionUtil {
	private ReflectionUtil() {}

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

	public static <T> Constructor<T> getBestConstructor(Class<T> clazz) throws NoSuchMethodException {
		Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
		if (constructors.length == 0)
			constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
		if (constructors.length == 0)
			return getAnyConstructor(clazz);
		for (Constructor<T> c : constructors)
			c.setAccessible(true);
		if (constructors.length == 1)
			return constructors[0];
		Arrays.sort(constructors, (Comparator.comparingInt(Constructor::getParameterCount)));
		return clazz.getConstructor(constructors[constructors.length - 1].getParameterTypes());
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
}
