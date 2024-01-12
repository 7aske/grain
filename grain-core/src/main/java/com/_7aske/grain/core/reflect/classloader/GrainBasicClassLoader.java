package com._7aske.grain.core.reflect.classloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com._7aske.grain.core.reflect.ReflectionUtil.loadClass;

public class GrainBasicClassLoader implements GrainClassLoader {
	private final String basePackage;
	private final ClassLoader classLoader;

	public GrainBasicClassLoader(String basePackage) {
		this.basePackage = basePackage;
		this.classLoader = ClassLoader.getSystemClassLoader();
	}

	public Set<Class<?>> loadClasses(Predicate<Class<?>> predicate) {
		return doLoadClasses(basePackage, predicate);
	}

	public Set<Class<?>> loadClasses() {
		return doLoadClasses(basePackage, c -> true);
	}

	private Set<Class<?>> doLoadClasses(String pkg, Predicate<Class<?>> predicate) {
		InputStream stream = classLoader.getResourceAsStream(pkg.replaceAll("[.]", "/"));

		if (stream == null) {
			return new HashSet<>();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		return reader.lines()
				.flatMap(line -> {
					if (line.endsWith(".class")) {
						return Stream.of(loadClass(line, pkg));
					} else {
						if (pkg.equals("")) {
							return doLoadClasses(line, predicate).stream();
						} else {
							return doLoadClasses(pkg + "." + line, predicate).stream();
						}
					}
				})
				.filter(Objects::nonNull)
				.filter(predicate)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

}
