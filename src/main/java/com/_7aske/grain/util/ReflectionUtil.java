package com._7aske.grain.util;

import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.Default;
import com._7aske.grain.core.component.Primary;
import com._7aske.grain.exception.GrainMultipleImplementationsException;
import com._7aske.grain.exception.GrainReflectionException;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.web.controller.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Collection of utilities related for reflective operations and type inspections.
 */
public class ReflectionUtil {
	private ReflectionUtil() {
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
	 * @param object      Field or Method on which to search annotation for
	 * @param annotations annotations to search for
	 * @return if annotation is present in the object or recursively in any of
	 * the annotated types
	 */
	public static boolean isAnyAnnotationPresent(AccessibleObject object, Class<? extends Annotation>... annotations) {
		return Arrays.stream(object.getAnnotations()).anyMatch(a -> Arrays.stream(annotations).anyMatch(a1 -> a1.equals(a.annotationType())));
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
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new GrainReflectionException(e);
		}
	}

	public static <T> Optional<T> newInstance(Constructor<T> constructor, Object... params) {
		try {
			return Optional.of(constructor.newInstance(params));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			return Optional.empty();
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
		return (Class<T>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
	}

	/**
	 * See {@link ReflectionUtil#compareLibraryAndUserPackage(String, String)}
	 */
	public static int compareLibraryAndUserPackage(Object o1, Object o2) {
		return compareLibraryAndUserPackage(o1.getClass().getPackageName(), o2.getClass().getPackageName());
	}

	/**
	 * See {@link ReflectionUtil#compareLibraryAndUserPackage(String, String)}
	 */
	public static int compareLibraryAndUserPackage(Class<?> c1, Class<?> c2) {
		return compareLibraryAndUserPackage(c1.getPackageName(), c2.getPackageName());
	}

	/**
	 * Comparator that is used for sorting classes or objects by its packageName
	 * so that the resulting sorted list starts with classes or objects that are
	 * not grain-library defined.
	 *
	 * @param c1Package comparable first argument package
	 * @param c2Package comparable second argument package
	 * @return compared packages
	 */
	public static int compareLibraryAndUserPackage(String c1Package, String c2Package) {
		String basePackagePrefix = GrainApp.class.getPackageName() + ".";
		// @Refactor can this be done better?
		if (!c1Package.startsWith(basePackagePrefix) && !c2Package.startsWith(basePackagePrefix))
			return 0;
		if (c1Package.startsWith(basePackagePrefix) && c2Package.startsWith(basePackagePrefix))
			return 0;
		if (c1Package.startsWith(basePackagePrefix)) return 1;
		if (c2Package.startsWith(basePackagePrefix)) return -1;
		return 0;
	}

	public static <T> List<Class<?>> findClasses(Class<?> clazz, Collection<T> classes, Function<T, Class<?>> extractor) {
		return classes.stream()
				.map(extractor::apply)
				.filter(d -> d.equals(clazz) || clazz.isAssignableFrom(d))
				.collect(Collectors.toList());
	}

	/**
	 * Method used to search a collection of items for the appropriate class.
	 * Used when given a collection of Grains or Dependencies we want to find
	 * an object or dependency that matches provided search class. It prioritizes
	 * on the objects or dependencies of types defined by the user rather than
	 * the library itself.
	 *
	 * @param clazz     Class we search for.
	 * @param classes   Collection of items where to search for a given class.
	 * @param extractor Lambda that defined how the class is extracted from the
	 *                  generic Collection item.
	 * @param <T>       Type of the list items.
	 * @return Optional of the found class.
	 */
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
						// letter after the basePackage is a dot since in case of com._7aske.grain as basePackage and
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

	/**
	 * Extracts handler path from any of the valid @RequestMapping annotations.
	 *
	 * @param method to extract the path from
	 * @return extracted request handler path. Throws if the annotation is not found
	 */
	public static String getAnnotatedHttpPath(Method method) {
		if (method.isAnnotationPresent(RequestMapping.class))
			return method.getAnnotation(RequestMapping.class).value();
		if (method.isAnnotationPresent(GetMapping.class))
			return method.getAnnotation(GetMapping.class).value();
		if (method.isAnnotationPresent(PostMapping.class))
			return method.getAnnotation(PostMapping.class).value();
		if (method.isAnnotationPresent(PutMapping.class))
			return method.getAnnotation(PutMapping.class).value();
		if (method.isAnnotationPresent(DeleteMapping.class))
			return method.getAnnotation(DeleteMapping.class).value();
		if (method.isAnnotationPresent(PatchMapping.class))
			return method.getAnnotation(PatchMapping.class).value();
		if (method.isAnnotationPresent(HeadMapping.class))
			return method.getAnnotation(HeadMapping.class).value();
		if (method.isAnnotationPresent(TraceMapping.class))
			return method.getAnnotation(TraceMapping.class).value();
		throw new GrainRuntimeException("Method not annotated with a valid @RequestMapping annotation");
	}

	/**
	 * Extracts handler path from any of the valid @RequestMapping annotations.
	 * Decided not to throw and rather return null since the controller Grain
	 * can have it path defaulted to being just a "/".
	 *
	 * @param clazz to extract the path from
	 * @return extracted request handler path.
	 */
	public static String getAnnotatedHttpPath(Class<?> clazz) {
		if (clazz.isAnnotationPresent(RequestMapping.class))
			return clazz.getAnnotation(RequestMapping.class).value();
		if (clazz.isAnnotationPresent(GetMapping.class))
			return clazz.getAnnotation(GetMapping.class).value();
		if (clazz.isAnnotationPresent(PostMapping.class))
			return clazz.getAnnotation(PostMapping.class).value();
		if (clazz.isAnnotationPresent(PutMapping.class))
			return clazz.getAnnotation(PutMapping.class).value();
		if (clazz.isAnnotationPresent(DeleteMapping.class))
			return clazz.getAnnotation(DeleteMapping.class).value();
		if (clazz.isAnnotationPresent(PatchMapping.class))
			return clazz.getAnnotation(PatchMapping.class).value();
		if (clazz.isAnnotationPresent(HeadMapping.class))
			return clazz.getAnnotation(HeadMapping.class).value();
		if (clazz.isAnnotationPresent(TraceMapping.class))
			return clazz.getAnnotation(TraceMapping.class).value();
		return null;
	}

	/**
	 * Extracts http handler method from any of the valid @RequestMapping annotations.
	 *
	 * @param method to extract the http method from
	 * @return extracted request handler path. Throws if the annotation is not found
	 */
	public static HttpMethod getAnnotatedHttpMethod(Method method) {
		if (method.isAnnotationPresent(RequestMapping.class))
			return method.getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(GetMapping.class))
			return method.getAnnotation(GetMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(PostMapping.class))
			return method.getAnnotation(PostMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(PutMapping.class))
			return method.getAnnotation(PutMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(DeleteMapping.class))
			return method.getAnnotation(DeleteMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(PatchMapping.class))
			return method.getAnnotation(PatchMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(HeadMapping.class))
			return method.getAnnotation(HeadMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (method.isAnnotationPresent(TraceMapping.class))
			return method.getAnnotation(TraceMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		throw new GrainRuntimeException("Method not annotated with a valid @RequestMapping annotation");
	}

	/**
	 * Extracts http handler method from any of the valid @RequestMapping annotations.
	 *
	 * @param clazz to extract the http method from
	 * @return extracted request handler path. Throws if the annotation is not found
	 */
	public static HttpMethod getAnnotatedHttpMethod(Class<?> clazz) {
		if (clazz.isAnnotationPresent(RequestMapping.class))
			return clazz.getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(GetMapping.class))
			return clazz.getAnnotation(GetMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(PostMapping.class))
			return clazz.getAnnotation(PostMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(PutMapping.class))
			return clazz.getAnnotation(PutMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(DeleteMapping.class))
			return clazz.getAnnotation(DeleteMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(PatchMapping.class))
			return clazz.getAnnotation(PatchMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(HeadMapping.class))
			return clazz.getAnnotation(HeadMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		if (clazz.isAnnotationPresent(TraceMapping.class))
			return clazz.getAnnotation(TraceMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
		throw new GrainRuntimeException("Method not annotated with a valid @RequestMapping annotation");
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
	 * Invokes a method regardless of its visibility and accessibility.
	 */
	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			method.setAccessible(true);
			return method.invoke(target, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new GrainReflectionException(String.format("Unable to invoke method '%s'", method.getName()), e);
		}
	}
}
