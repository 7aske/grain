package com._7aske.grain.component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com._7aske.grain.util.ReflectionUtil.loadClass;

public class GrainLoader {
	private final String basePackage;
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	public GrainLoader(String basePackage) {
		this.basePackage = basePackage;
	}

	public Set<Class<?>> loadGrains() {
		return doLoadGrains(basePackage);
	}

	private Set<Class<?>> doLoadGrains(String pkg) {
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
						return doLoadGrains(pkg + "." + line).stream();
					}
				})
				.filter(Objects::nonNull)
				.filter(c -> {
					if (c.isAnnotationPresent(Grain.class)) return true;
					// check if annotation is inherited
					return Arrays.stream(c.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(Grain.class));
				})
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
