package com._7aske.grain.core.component;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

class BetterDependency {
	private final String name;
	private final Class<?> type;
	private final Constructor<?> constructor;
	private final DependencyReference[] constructorParameters;
	private Object instance;
	private final List<Method> grainMethods;
	private final List<Field> injectableFields;
	private List<DependencyReference> dependencies;
	private final List<BetterDependencyField> valueFields;
	private final List<Method> afterInitMethods;
	/**
	 * Dependency that provides this dependency via the @Grain annotated method.
	 * Should be resolved and initialized before this dependency is initialized.
	 */
	private BetterDependency provider;

	private BetterDependency(Class<?> type, String name, Constructor<?> constructor) {
		this.name = name;
		this.type = type;
		this.constructor = constructor;
		this.constructorParameters = new DependencyReference[0];
		this.dependencies = new ArrayList<>();
		this.grainMethods = new ArrayList<>();
		this.injectableFields = new ArrayList<>();
		this.valueFields = new ArrayList<>();
		this.afterInitMethods = new ArrayList<>();
	}

	public static BetterDependency ofMethod(@NotNull Class<?> clazz, @Nullable String name, BetterDependency provider) {
		BetterDependency betterDependency = new BetterDependency(clazz, name, null);
		betterDependency.setProvider(provider);
		return betterDependency;
	}

	public BetterDependency(@NotNull Class<?> clazz, @Nullable String name) {
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

		this.injectableFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Inject.class))
				.collect(Collectors.toList());

		List<DependencyReference> constructorDependencies = Arrays.asList(this.constructorParameters);
		List<DependencyReference> grainMethodDependencies = this.grainMethods.stream()
				.flatMap(m -> Arrays.stream(m.getParameters()).map(DependencyReference::of))
				.collect(Collectors.toList());
		List<DependencyReference> grainFieldDependencies = this.injectableFields.stream()
				.map(DependencyReference::of)
				.collect(Collectors.toList());

		this.dependencies = new ArrayList<>();
		this.dependencies.addAll(constructorDependencies);
		// this.dependencies.addAll(grainMethodDependencies);
		// this.dependencies.addAll(grainFieldDependencies);

		this.type = clazz;
		this.instance = null;
		this.provider = null;
		this.valueFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(BetterDependencyField::new)
				.collect(Collectors.toList());
		this.afterInitMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, AfterInit.class))
				.collect(Collectors.toList());
	}

	public <T> T getInstance() {
		return (T) instance;
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

	public List<Method> getGrainMethods() {
		return grainMethods;
	}

	public List<Field> getInjectableFields() {
		return injectableFields;
	}

	public boolean isInitialized() {
		return instance != null;
	}

	public boolean isGrainMethodDependency() {
		return provider != null;
	}

	public BetterDependency getProvider() {
		return provider;
	}

	public void setProvider(BetterDependency provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", type.getName(), name);
	}

	public List<BetterDependencyField> getValueFields() {
		return valueFields;
	}

	public List<Method> getAfterInitMethods() {
		return afterInitMethods;
	}

	@Override
	public boolean equals(Object o) {
		return o == this;
	}
}
