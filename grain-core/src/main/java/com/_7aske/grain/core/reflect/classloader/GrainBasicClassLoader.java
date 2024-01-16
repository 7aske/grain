package com._7aske.grain.core.reflect.classloader;

import com._7aske.grain.exception.GrainRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
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

	@Override
	public Set<Class<?>> loadClasses(Predicate<Class<?>> predicate) {
		return doLoadClasses(basePackage, predicate);
	}

	@Override
	public Set<Class<?>> loadClasses() {
		return doLoadClasses(basePackage, c -> true);
    }

	private Set<Class<?>> doLoadClasses(String currentPackage, Predicate<Class<?>> predicate) {
		InputStream stream = classLoader.getResourceAsStream(currentPackage.replaceAll("[.]", "/"));

		if (stream == null) {
			return new HashSet<>();
		}

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                    .flatMap(classOrPackage -> {
                        if (classOrPackage.endsWith(".class")) {
                            return Stream.of(loadClass(classOrPackage, currentPackage));
                        }

						String pkg = classOrPackage;
						if (!currentPackage.isEmpty()) {
							pkg = currentPackage + "." + classOrPackage;
						}

						return doLoadClasses(pkg, predicate).stream();
                    })
                    .filter(Objects::nonNull)
                    .filter(predicate)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IOException e) {
            throw new GrainRuntimeException(e);
        }
    }

}
