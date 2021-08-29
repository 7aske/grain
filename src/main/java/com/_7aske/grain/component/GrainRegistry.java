package com._7aske.grain.component;

import com._7aske.grain.util.ReflectionUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrainRegistry {
	private Set<Object> grains = new HashSet<>();
	private final String basePackage;

	public GrainRegistry(String pkg) {
		if (pkg == null)
			throw new IllegalArgumentException("Base package must not be null");
		this.basePackage = pkg;
		doInitializeGrains();
	}

	private Set<Class<?>> getGrains(Package pkg) {
		InputStream stream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(pkg.getName().replaceAll("[.]", "/"));

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		return reader.lines()
				.filter(line -> line.endsWith(".class"))
				.map(line -> getClass(line, basePackage))
				.filter(Objects::nonNull)
				.filter(c -> {
					if (c.isAnnotationPresent(Grain.class)){
						return true;
					}
					return Arrays.stream(c.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(Grain.class));
				})
				.collect(Collectors.toSet());
	}

	private void doInitializeGrains() {
		Package[] packages = Package.getPackages();

		List<Package> ownPackages = Arrays.stream(packages)
				.filter(pkg -> pkg.getName().startsWith(basePackage))
				.collect(Collectors.toList());

		Set<Class<?>> grainClasses = ownPackages.stream()
				.flatMap(pkg -> {
					Set<Class<?>> stream = getGrains(pkg);
					return stream.stream();
				})
				.collect(Collectors.toSet());

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
			return Class.forName(packageName + "."
					+ className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
			// handle the exception
		}
		return null;
	}

	public Set<Object> getControllers() {
		return grains.stream()
				.filter(g -> g.getClass().isAnnotationPresent(Controller.class))
				.collect(Collectors.toSet());
	}
}