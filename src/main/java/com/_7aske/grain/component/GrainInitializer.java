package com._7aske.grain.component;

import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.*;

public class GrainInitializer {
	private final Set<Dependency> dependencies;

	public GrainInitializer() {
		this.dependencies = new HashSet<>();
	}

	public Map<Class<?>, Object> initialize(Set<Class<?>> classes) {
		this.dependencies.addAll(classes.stream().map(Dependency::new).collect(Collectors.toList()));
		this.dependencies.forEach(this::initializeConstructors);
		this.dependencies.forEach(this::loadOwnDependencies);
		this.dependencies.forEach(this::lazyInitialize);
		this.dependencies.forEach(this::initializeSkippedFields);
		return this.dependencies.stream().collect(Collectors.toMap(Dependency::getClazz, Dependency::getInstance));
	}

	public Object addInitialized(Object object) {
		Dependency dependency = new Dependency(object.getClass());
		dependency.instance = object;
		dependency.visited = true;
		dependency.initialized = true;
		this.dependencies.add(dependency);
		return object;
	}

	private void initializeConstructors(Dependency dependency) {
		try {
			if (dependency.clazz.isInterface()) {
				Dependency resolved = dependencies.stream()
						.filter(dep -> {
							if (dep.clazz.equals(dependency.clazz)) return false;
							return haveCommonInterfaces(dep.clazz, dependency.clazz);
						})
						.findFirst()
						.orElseThrow(NoSuchMethodException::new);
				dependency.constructor = resolved.constructor;
				dependency.params = resolved.params;
			} else {
				dependency.constructor = getBestConstructor(dependency.clazz);
				dependency.params = dependency.constructor.getParameterTypes();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public Object initialize(Class<?> clazz) {
			Dependency dependency = new Dependency(clazz);
			this.initializeConstructors(dependency);
			this.loadOwnDependencies(dependency);
			this.lazyInitialize(dependency);
			this.initializeSkippedFields(dependency);
			this.dependencies.add(dependency);
			return dependency.instance;
	}

	private void loadOwnDependencies(Dependency dependency) {
		if (dependency.initialized) return;
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
					Dependency dependency = findDependencyByClass(field.getType())
							.orElseThrow(() -> new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s: %s", dep.clazz, field.getType())));
					lazyInitialize(dependency);
					field.set(dep.instance, dependency.instance);
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
				return Arrays.asList(d.clazz.getInterfaces()).contains(clazz) || d.clazz.equals(clazz);
			} else {
				return d.clazz.equals(clazz);
			}
		}).findFirst();
	}

	private void lazyInitialize(Dependency dep) {
		if (dep.visited || dep.initialized) return;
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
				dep.initialized = true;
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new GrainInitializationException(String.format("Could not instantiate grain %s.", dep.clazz), e);
			}
		}
	}

	private Dependency[] mapParamsToDependencies(Class<?>[] params, Set<Dependency> allDependencies) {
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

	private Dependency[] mapParamsToDependencies(Dependency dependency, Set<Dependency> allDependencies) throws NoSuchMethodException {
		return mapParamsToDependencies(dependency.constructor.getParameterTypes(), allDependencies);
	}

	private static final class Dependency {
		boolean visited;
		boolean initialized;
		Class<?> clazz;
		Constructor<?> constructor;
		Class<?>[] params;
		Object instance;
		List<Dependency> dependencies;

		public Dependency(Class<?> clazz) {
			this(clazz, new ArrayList<>());
		}

		public Dependency(Class<?> clazz, List<Dependency> dependencies) {
			this.constructor = null;
			this.params = null;
			this.clazz = clazz;
			this.dependencies = dependencies;
			this.visited = false;
			this.instance = null;
			this.initialized = false; // for manually initialized grains
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public Object getInstance() {
			return instance;
		}
	}
}
