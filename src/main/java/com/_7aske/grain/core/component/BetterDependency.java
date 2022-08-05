package com._7aske.grain.core.component;

import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

class BetterDependency {
	private boolean visited;
	private boolean initialized = false;
	private boolean isGrainMethodDependency;
	private final String name;
	private final Class<?> type;
	private final Constructor<?> constructor;
	private final DependencyReference[] constructorParameters;
	private final List<Method> grainMethods;
	private Object instance;
	private boolean lifecycleMethodCalled;
	private final List<BetterDependencyField> valueFields;
	private List<DependencyReference> dependencies;
	private final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();
	private BetterDependency provider;

	public BetterDependency(Class<?> clazz, @Nullable String name) {
		this.name = name;
		try {
			this.constructor = ReflectionUtil.getBestConstructor(clazz);
		} catch (NoSuchMethodException e) {
			throw new GrainInitializationException("No constructor found for class " + clazz.getName());
		}
		this.constructorParameters = new DependencyReference[this.constructor.getParameterCount()];
		Parameter[] parameters = this.constructor.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			this.constructorParameters[i] = DependencyReference.of(parameters[i]);
		}

		this.grainMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, Grain.class))
				.collect(Collectors.toList());

		this.dependencies = new ArrayList<>(Arrays.asList(this.constructorParameters));
		List<DependencyReference> grainMethodDependencies = this.grainMethods.stream()
				.flatMap(m -> Arrays.stream(m.getParameters()).map(DependencyReference::of))
				.collect(Collectors.toList());
		this.dependencies.addAll(grainMethodDependencies);

		this.type = clazz;
		this.visited = false;
		this.instance = null;
		this.initialized = false; // for manually initialized grains
		this.lifecycleMethodCalled = false;
		this.valueFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(BetterDependencyField::new)
				.collect(Collectors.toList());
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public DependencyReference[] getConstructorParameters() {
		return constructorParameters;
	}

	public List<DependencyReference> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<DependencyReference> dependencies) {
		this.dependencies = dependencies;
	}

	public Class<?> getType() {
		return type;
	}

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	public DependencyReference asReference() {
		if (name == null) {
			return DependencyReference.byType(type);
		} else {
			return DependencyReference.byName(type, name);
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean b) {
		this.initialized = b;
	}

	public List<Method> getGrainMethods() {
		return grainMethods;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", type.getName(), name);
	}

	public boolean isGrainMethodDependency() {
		return isGrainMethodDependency;
	}

	public void setGrainMethodDependency(boolean grainMethodDependency) {
		isGrainMethodDependency = grainMethodDependency;
	}

	public BetterDependency getProvider() {
		return provider;
	}

	public void setProvider(BetterDependency provider) {
		this.provider = provider;
	}
}
