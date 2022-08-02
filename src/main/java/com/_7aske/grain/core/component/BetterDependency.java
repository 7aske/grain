package com._7aske.grain.core.component;

import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

class BetterDependency {
	private boolean visited;
	private boolean initialized;
	private final String name;
	private final Class<?> clazz;
	private Constructor<?> constructor;
	private final Parameter[] params;
	private Object instance;
	private boolean lifecycleMethodCalled;
	private final List<BetterDependencyField> valueFields;
	private List<String> dependencies;
	private String provider;

	public BetterDependency(String name, Class<?> clazz) {
		this.name = name;
		try {
			this.constructor = ReflectionUtil.getBestConstructor(clazz);
		} catch (NoSuchMethodException e) {
			throw new GrainInitializationException("No constructor found for class " + clazz.getName());
		}
		this.params = this.constructor.getParameters();
		this.clazz = clazz;
		this.visited = false;
		this.instance = null;
		this.initialized = false; // for manually initialized grains
		this.lifecycleMethodCalled = false;
		this.valueFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(BetterDependencyField::new)
				.collect(Collectors.toList());
	}

	public Parameter[] getParams() {
		return params;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
