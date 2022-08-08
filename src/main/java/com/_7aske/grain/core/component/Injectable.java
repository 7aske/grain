package com._7aske.grain.core.component;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

class Injectable<T> {
	private final String name;
	private final Class<T> type;
	private final Constructor<T> constructor;
	private final InjectableReference<?>[] constructorParameters;
	private T instance;
	private final List<Method> grainMethods;
	private final List<Field> injectableFields;
	private List<InjectableReference<?>> dependencies;
	private final List<InjectableField> valueFields;
	private final List<Method> afterInitMethods;
	/**
	 * Dependency that provides this dependency via the @Grain annotated method.
	 * Should be resolved and initialized before this dependency is initialized.
	 */
	private Injectable<?> provider;

	private Injectable(Class<T> type, String name, Constructor<T> constructor) {
		this.name = name;
		this.type = type;
		this.constructor = constructor;
		this.constructorParameters = new InjectableReference[0];
		this.dependencies = new ArrayList<>();
		this.grainMethods = new ArrayList<>();
		this.injectableFields = new ArrayList<>();
		this.valueFields = new ArrayList<>();
		this.afterInitMethods = new ArrayList<>();
	}

	public static <T> Injectable<T> ofMethod(@NotNull Class<T> clazz, @Nullable String name, Injectable<?> provider) {
		Injectable<T> injectable = new Injectable<>(clazz, name, null);
		injectable.setProvider(provider);
		return injectable;
	}

	public Injectable(@NotNull Class<T> clazz, @Nullable String name) {
		this.name = name;
		if (clazz.isInterface()) {
			this.constructor = null;
			this.constructorParameters = new InjectableReference[0];
			this.instance = ReflectionUtil.createProxy(clazz);
		} else {
			try {
				this.constructor = ReflectionUtil.getBestConstructor(clazz);
				this.constructorParameters = new InjectableReference[this.constructor.getParameterCount()];
				Parameter[] parameters = this.constructor.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					this.constructorParameters[i] = InjectableReference.of(parameters[i]);
				}
			} catch (NoSuchMethodException e) {
				throw new GrainInitializationException("No constructor found for class " + clazz.getName());
			}
		}


		this.grainMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, Grain.class))
				.collect(Collectors.toList());

		this.injectableFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Inject.class))
				.collect(Collectors.toList());

		List<InjectableReference<?>> constructorDependencies = Arrays.asList(this.constructorParameters);
		// List<DependencyReference> grainMethodDependencies = this.grainMethods.stream()
		// 		.flatMap(m -> Arrays.stream(m.getParameters()).map(DependencyReference::of))
		// 		.collect(Collectors.toList());
		// List<DependencyReference> grainFieldDependencies = this.injectableFields.stream()
		// 		.map(DependencyReference::of)
		// 		.collect(Collectors.toList());

		this.dependencies = new ArrayList<>();
		this.dependencies.addAll(constructorDependencies);
		// this.dependencies.addAll(grainMethodDependencies);
		// this.dependencies.addAll(grainFieldDependencies);

		this.type = clazz;
		this.provider = null;
		this.valueFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(InjectableField::new)
				.collect(Collectors.toList());
		this.afterInitMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, AfterInit.class))
				.collect(Collectors.toList());
	}

	public T getInstance() {
		return instance;
	}

	public void setInstance(T instance) {
		this.instance = instance;
	}

	public void setObjectInstance(Object instance) {
		this.instance = type.cast(instance);
	}

	public Constructor<T> getConstructor() {
		return constructor;
	}

	public InjectableReference<?>[] getConstructorParameters() {
		return constructorParameters;
	}

	public List<InjectableReference<?>> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<InjectableReference<?>> dependencies) {
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

	public Injectable<?> getProvider() {
		return provider;
	}

	public void setProvider(Injectable<?> provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", type.getName(), name);
	}

	public List<InjectableField> getValueFields() {
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
