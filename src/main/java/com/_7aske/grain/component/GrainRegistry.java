package com._7aske.grain.component;

import com._7aske.grain.requesthandler.middleware.Middleware;
import com._7aske.grain.util.ReflectionUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrainRegistry {
	private Set<Object> grains = new HashSet<>();
	private final String basePackage;
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	public GrainRegistry(String pkg) {
		if (pkg == null)
			throw new IllegalArgumentException("Base package must not be null");
		this.basePackage = pkg;
		doInitializeGrains();
	}

	private Set<Class<?>> loadGrains(String pkg) {
		InputStream stream = classLoader.getResourceAsStream(pkg.replaceAll("[.]", "/"));

		if (stream == null) {
			return new HashSet<>();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		return reader.lines()
				.flatMap(line -> {
					if (line.endsWith(".class")) {
						return Stream.of(getClass(line, pkg));
					} else {
						return loadGrains(pkg + "." + line).stream();
					}
				})
				.filter(Objects::nonNull)
				.filter(c -> {
					if (c.isAnnotationPresent(Grain.class)) return true;
					// check if annotation is inherited
					return Arrays.stream(c.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(Grain.class));
				})
				.collect(Collectors.toSet());
	}

	private void doInitializeGrains() {
		Set<Class<?>> grainClasses = loadGrains(basePackage);

		grains = grainClasses.stream()
				.map(c -> {
					try {
						Constructor<?> constructor = ReflectionUtil.getAnyConstructor(c);
						return constructor.newInstance();
					} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private Class<?> getClass(String className, String packageName) {
		try {
			String fullClassName = packageName + "." + className.substring(0, className.lastIndexOf('.'));
			return Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public Set<Object> getControllers() {
		return grains.stream()
				.filter(g -> g.getClass().isAnnotationPresent(Controller.class))
				.collect(Collectors.toSet());
	}

	public Set<Object> getGrains() {
		return grains;
	}

	public Set<Middleware> getMiddlewares() {
		return grains.stream()
				.filter(g -> Arrays.asList(g.getClass().getInterfaces()).contains(Middleware.class))
				.map(Middleware.class::cast)
				.collect(Collectors.toSet());
	}
}