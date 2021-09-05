package com._7aske.grain.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.getBestConstructor;
import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class GrainInitializer {
	private final Map<Class<?>, Object> instances = new HashMap<>();
	private final List<Dependency> dependencies;

	public GrainInitializer(Set<Class<?>> grains) {
		this.dependencies = grains.stream()
				.map(Dependency::new)
				.collect(Collectors.toList());

		dependencies.forEach(this::mapConstructorParamsToDependencies);
		dependencies.forEach(this::lazyInitialize);
		dependencies.forEach(this::initializeSkippedFields);
		dependencies.forEach(dep -> instances.put(dep.clazz, dep.instance));
		dependencies.clear();
	}

	private void mapConstructorParamsToDependencies(Dependency dependency) {

		try {
			dependency.dependencies = Arrays.stream(getBestConstructor(dependency.clazz).getParameterTypes())
					.map(param -> dependencies.stream().filter(dep -> dep.clazz.equals(param)).findFirst().orElse(null))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private void initializeSkippedFields(Dependency dep) {
		Class<?> clazz = dep.clazz;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				if (field.get(dep.instance) == null && isAnnotationPresent(field.getType(), Grain.class)) {
					Optional<Dependency> dependencyOptional = findDependencyByClass(field.getType());
					if (dependencyOptional.isPresent()) {
						Dependency dependency = dependencyOptional.get();
						lazyInitialize(dependency);
						field.set(dep.instance, dependency.instance);
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			field.setAccessible(false);
		}
	}

	private Optional<Dependency> findDependencyByClass(Class<?> clazz) {
		return dependencies.stream().filter(d -> d.clazz.equals(clazz)).findFirst();
	}

	private void lazyInitialize(Dependency dep) {
		if (dep.visited) return;
		dep.visited = true;
		if (dep.instance == null) {
			try {
				Constructor<?> constructor = getBestConstructor(dep.clazz);
				Class<?>[] params = constructor.getParameterTypes();
				Dependency[] deps = mapParamsToDependencies(params, dependencies);

				Object[] realParams = new Object[params.length];
				for (int i = 0; i < params.length; i++) {
					lazyInitialize(deps[i]);
					realParams[i] = deps[i].instance;
				}
				dep.instance = constructor.newInstance(realParams);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private Dependency[] mapParamsToDependencies(Class<?>[] params, List<Dependency> allDependencies) {
		return Arrays.stream(params)
				.map(p -> allDependencies.stream().filter(d -> d.clazz.equals(p)).findFirst().orElse(null))
				.toArray(Dependency[]::new);
	}

	public Map<Class<?>, Object> getLoaded() {
		try {
			return new HashMap<>(instances);
		} finally {
			instances.clear();
		}
	}

	private static final class Dependency {
		boolean visited;
		Class<?> clazz;
		List<Dependency> dependencies;
		Object instance;

		public Dependency(Class<?> clazz) {
			this(clazz, new ArrayList<>());
		}

		public Dependency(Class<?> clazz, List<Dependency> dependencies) {
			this.clazz = clazz;
			this.dependencies = dependencies;
			this.visited = false;
			this.instance = null;
		}
	}
}
