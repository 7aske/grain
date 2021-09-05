package com._7aske.grain.component;

import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;

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
				.map(g -> {
					try {
						return new Dependency(g);
					} catch (NoSuchMethodException ex) {
						throw new GrainInitializationException(String.format("Unable to find constructor for %s", g), ex);
					}
				})
				.collect(Collectors.toList());

		this.dependencies.forEach(this::loadOwnDependencies);
		this.dependencies.forEach(this::lazyInitialize);
		this.dependencies.forEach(this::initializeSkippedFields);
		this.dependencies.forEach(dep -> instances.put(dep.clazz, dep.instance));
		this.dependencies.clear();
	}

	private void loadOwnDependencies(Dependency dependency) {
		try {
			Dependency[] deps = mapParamsToDependencies(dependency, this.dependencies);
			if (Arrays.stream(deps).anyMatch(Objects::isNull)) {
				throw new GrainDependencyUnsatisfiedException(dependency.clazz, dependency.params);
			}
			dependency.dependencies = List.of(deps);
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
				if (field.get(dep.instance) == null && (isAnnotationPresent(field, Inject.class) || (isConstructorParam(dep.constructor, field.getType())))) {
					Optional<Dependency> dependencyOptional = findDependencyByClass(field.getType());
					if (dependencyOptional.isPresent()) {
						Dependency dependency = dependencyOptional.get();
						lazyInitialize(dependency);
						field.set(dep.instance, dependency.instance);
					} else {
						throw new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s", dep.clazz));
					}
				}
			} catch (IllegalAccessException e) {
				throw new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s", dep.clazz), e);
			}
			field.setAccessible(false);
		}
	}

	private boolean isConstructorParam(Constructor<?> constructor, Class<?> type) {
		return List.of(constructor.getParameterTypes()).contains(type) && isAnnotationPresent(type, Grain.class);
	}

	private Optional<Dependency> findDependencyByClass(Class<?> clazz) {
		return dependencies.stream().filter(d -> {
			if (clazz.isInterface()) {
				return Arrays.asList(d.clazz.getInterfaces()).contains(clazz);
			} else {
				return d.clazz.equals(clazz);
			}
		}).findFirst();
	}

	private void lazyInitialize(Dependency dep) {
		if (dep.visited) return;
		dep.visited = true;
		if (dep.instance == null) {
			try {
				Dependency[] deps = mapParamsToDependencies(dep.params, dependencies);

				Object[] realParams = new Object[dep.params.length];
				for (int i = 0; i < dep.params.length; i++) {
					lazyInitialize(deps[i]);
					realParams[i] = deps[i].instance;
				}
				dep.instance = dep.constructor.newInstance(realParams);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new GrainInitializationException(String.format("Could not instantiate grain %s.", dep.clazz), e);
			}
		}
	}

	private Dependency[] mapParamsToDependencies(Class<?>[] params, List<Dependency> allDependencies) {
		return Arrays.stream(params)
				.map(param -> {
					if (param.isInterface()) {
						return allDependencies.stream()
								.filter(dep -> Arrays.asList(dep.clazz.getInterfaces()).contains(param))
								.findFirst()
								.orElse(null);
					} else {
						return allDependencies.stream()
								.filter(dep -> dep.clazz.equals(param))
								.findFirst()
								.orElse(null);
					}
				})
				.toArray(Dependency[]::new);
	}

	private Dependency[] mapParamsToDependencies(Dependency dependency, List<Dependency> allDependencies) throws NoSuchMethodException {
		return mapParamsToDependencies(getBestConstructor(dependency.clazz).getParameterTypes(), allDependencies);
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
		Constructor<?> constructor;
		Class<?>[] params;
		Object instance;
		List<Dependency> dependencies;

		public Dependency(Class<?> clazz) throws NoSuchMethodException {
			this(clazz, new ArrayList<>());
		}

		public Dependency(Class<?> clazz, List<Dependency> dependencies) throws NoSuchMethodException {
			this.constructor = getBestConstructor(clazz);
			this.params = constructor.getParameterTypes();
			this.clazz = clazz;
			this.dependencies = dependencies;
			this.visited = false;
			this.instance = null;
		}
	}
}
