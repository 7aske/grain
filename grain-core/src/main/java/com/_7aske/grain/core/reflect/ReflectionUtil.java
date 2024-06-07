package com._7aske.grain.core.reflect;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.exception.GrainReflectionException;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Collection of utilities related for reflective operations and type inspections.
 */
public class ReflectionUtil {
	private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
	private ReflectionUtil() {
	}

	public static <T> @NotNull Constructor<T> getAnyConstructor(Class<T> clazz) throws NoSuchMethodException {
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

	public static <T> @NotNull Constructor<T> getBestConstructor(Class<T> clazz) throws NoSuchMethodException {
		try {
			return getAnyConstructor(clazz);
		} catch (NoSuchMethodException ignored) { /* ignored */ }
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


	/**
	 * Class.forName wrapper
	 *
	 * @return Successfully loaded class. Null if failed.
	 */
	public static Class<?> loadClass(String className, String packageName) {
		try {
			String fullClassName = packageName + "." + className.substring(0, className.lastIndexOf('.'));
			return Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * @param clazz      type on which to search annotation for
	 * @param annotation annotation to search for
	 * @return if annotation is present in the type or recursively in any of
	 * the annotated types
	 */
	public static boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotation) {
		if (clazz.equals(annotation)) return true;
		if (clazz.isAnnotationPresent(annotation)) return true;
		// Prevent infinite recursion
		if (List.of(Target.class, Retention.class, Documented.class).contains(clazz)) {
			return false;
		}
		// Check if annotation is composited in other annotations
		return Arrays.stream(clazz.getAnnotations()).anyMatch(a -> isAnnotationPresent(a.annotationType(), annotation));
	}

	/**
	 * @param object     Field or Method on which to search annotation for
	 * @param annotation annotation to search for
	 * @return if annotation is present in the object or recursively in any of
	 * the annotated types
	 */
	public static boolean isAnnotationPresent(AccessibleObject object, Class<? extends Annotation> annotation) {
		if (object.isAnnotationPresent(annotation)) return true;

		return Arrays.stream(object.getAnnotations()).anyMatch(a -> isAnnotationPresent(a.annotationType(), annotation));
	}

	/**
	 * @param parameter  Parameter on which to search annotation for
	 * @param annotation annotation to search for
	 * @return if annotation is present in the parameter or recursively in any of
	 * the annotated types
	 */
	public static boolean isAnnotationPresent(Parameter parameter, Class<? extends Annotation> annotation) {
		if (parameter.isAnnotationPresent(annotation)) return true;

		return Arrays.stream(parameter.getAnnotations()).anyMatch(a -> isAnnotationPresent(a.annotationType(), annotation));
	}

	/**
	 * @param object      Field or Method on which to search annotation for.
	 * @param annotations Annotations to search for.
	 * @return If annotation is present in the object.
	 */
	public static boolean isAnyAnnotationPresent(AccessibleObject object, Class<? extends Annotation>... annotations) {
		return Arrays.stream(object.getAnnotations())
				.anyMatch(a -> Arrays.stream(annotations)
						.anyMatch(a1 -> a1.equals(a.annotationType())));
	}

	/**
	 * @param clazz       Class on which to search annotation for.
	 * @param annotations Annotations to search for.
	 * @return If annotation is present in the class.
	 */
	public static boolean isAnyAnnotationPresent(Class<?> clazz, Class<? extends Annotation>... annotations) {
		for (Class<? extends Annotation> annotation : annotations) {
			if (isAnnotationPresent(clazz, annotation)) return true;
		}

		return false;
	}

	/**
	 * Utility method to find any constructor for the class and create
	 * an instance of it
	 *
	 * @param clazz Class to create the instance of
	 * @param <T>   Type of the instance
	 * @return created instance
	 * @throws GrainReflectionException thrown if in any of the cases
	 *                                  the instance could not have been created.
	 */
	public static <T> T newInstance(Class<T> clazz) throws GrainReflectionException {
		try {
			return getAnyConstructor(clazz).newInstance();
		} catch (InstantiationException | IllegalAccessException |
		         InvocationTargetException | NoSuchMethodException e) {
			throw new GrainReflectionException(e);
		}
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... params) {
		if (constructor == null) {
			throw new GrainReflectionException("Constructor cannot be null");
		}

		try {
			return constructor.newInstance(params);
		} catch (InstantiationException
				 | IllegalAccessException
				 | InvocationTargetException e) {
			throw new GrainReflectionException("Could not instantiate '" + constructor.getDeclaringClass().getName() + "'", e);
		}
	}

	/**
	 * Returns the class representing the generic type in a container class
	 * e.g. calling for List&lt;String&gt; would return Class&lt;String&gt;.
	 *
	 * @param f   Generic container field
	 * @param <T> Generic type
	 * @return generic type class
	 */
	public static <T> Class<T> getGenericListTypeArgument(Field f) {
		return getGenericListTypeArgument(f, 0);
	}

	public static <T> Class<T> getGenericListTypeArgument(Field f, int index) {
		Object result = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[index];
		if (result instanceof Class) {
			return (Class<T>) result;
		}

		if (result instanceof ParameterizedType) {
			return (Class<T>) ((ParameterizedType) result).getRawType();
		}

		throw new GrainReflectionException("Could not get generic type argument for field " + f.getName());
	}

	/**
	 * Returns the class representing the generic type in a container class
	 * e.g. calling for List&lt;String&gt; would return Class&lt;String&gt;.
	 *
	 * @param m   Method return a generic type.
	 * @param <T> Generic type
	 * @return generic type class
	 */
	public static <T> Class<T> getGenericListTypeArgument(Method m) {
		return (Class<T>) ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0];
	}

	/**
	 * Returns the class representing the generic type in a container class
	 * e.g. calling for List&lt;String&gt; would return Class&lt;String&gt;.
	 *
	 * @param p   Generic method parameter.
	 * @param <T> Generic type
	 * @return generic type class
	 */
	public static <T> Class<T> getGenericListTypeArgument(Parameter p) {
		return getGenericListTypeArgument(p, 0);
	}

	public static <T> Class<T> getGenericListTypeArgument(Parameter p, int index) {
		Object result = ((ParameterizedType) p.getParameterizedType()).getActualTypeArguments()[index];
		if (result instanceof Class) {
			return (Class<T>) result;
		}

		if (result instanceof ParameterizedType parameterizedType) {
			return (Class<T>) parameterizedType.getRawType();
		}

		throw new GrainReflectionException("Could not get generic type argument for parameter " + p.getName());
	}


	/**
	 * Sets a value to a field regardless of its visibility and accessibility.
	 */
	public static void setFieldValue(Field field, Object target, Object value) {
		try {
			field.setAccessible(true);
			field.set(target, value);
		} catch (IllegalAccessException e) {
			throw new GrainReflectionException(e);
		}
	}

	/**
	 * Gets a value to a field regardless of its visibility and accessibility.
	 */
	public static Object getFieldValue(Field field, Object target) {
		try {
			field.setAccessible(true);
			return field.get(target);
		} catch (IllegalAccessException e) {
			throw new GrainReflectionException(e);
		}
	}

	/**
	 * Gets a value to a field regardless of its visibility and accessibility.
	 */
	public static Object getFieldValue(Object object, String fieldName) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new GrainReflectionException(e);
		}
	}

	/**
	 * Invokes a method regardless of its visibility and accessibility.
	 */
	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			method.setAccessible(true);
			return method.invoke(target, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new GrainReflectionException(String.format("Unable to invoke method '%s'", method.getName()), e.getCause());
		}
	}

	/**
	 * Creates a proxy object for provided interfaces that allows calling their
	 * default methods. Calling non-default methods will result in returning a
	 * null value and a warning will be logged.
	 *
	 * @param interfaces to create a proxy for
	 * @return proxy object
	 */
	@Deprecated
	public static <T> T createProxy(Class<?>... interfaces) {
		return (T) Proxy.newProxyInstance(CLASS_LOADER, interfaces, new ProxyInvocationHandler());
	}
}
