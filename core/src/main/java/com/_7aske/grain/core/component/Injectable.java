package com._7aske.grain.core.component;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.core.configuration.GrainApplication;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.ToIntFunction;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

class Injectable<T> implements Comparable<Injectable<T>> {
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
	private final int order;
	/**
	 * Dependency that provides this dependency via the @Grain annotated method.
	 * Should be resolved and initialized before this dependency is initialized.
	 */
	private Injectable<?> provider;

	private Injectable(Class<T> type, String name, Constructor<T> constructor, int order) {
		this.name = name;
		this.type = type;
		this.constructor = constructor;
		this.constructorParameters = new InjectableReference[0];
		this.dependencies = new ArrayList<>();
		this.grainMethods = new ArrayList<>();
		this.injectableFields = new ArrayList<>();
		this.valueFields = new ArrayList<>();
		this.afterInitMethods = new ArrayList<>();
		this.order = order;
	}

	public static Injectable<?> ofMethod(@NotNull Method method, @Nullable String name, Injectable<?> provider) {
		int order = Optional.ofNullable(method.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);
		Injectable<?> injectable = new Injectable<>(method.getReturnType(), name, null, order);
		injectable.setProvider(provider);
		return injectable;
	}

	public static Injectable<?> ofInitialized(Object grain) {
		return new Injectable<>(grain.getClass(), null, null, 0);
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
				if (this.constructor == null) {
					throw new GrainInitializationException("No constructor found for class " + clazz.getName());
				}
				this.constructorParameters = new InjectableReference[this.constructor.getParameterCount()];
				Parameter[] parameters = this.constructor.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					this.constructorParameters[i] = InjectableReference.of(parameters[i]);
				}
			} catch (NoSuchMethodException e) {
				throw new GrainInitializationException("No constructor found for class " + clazz.getName());
			}
		}

		this.order = Optional.ofNullable(clazz.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);


		this.grainMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, Grain.class))
				.toList();

		this.injectableFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Inject.class))
				.toList();

		List<InjectableReference<?>> constructorDependencies = Arrays.asList(this.constructorParameters);

		this.dependencies = new ArrayList<>();
		// Filter added to prevent the Injectable to depend on itself
		List<InjectableReference<?>> references = constructorDependencies.stream()
				.filter(d -> !d.getType().isAssignableFrom(clazz))
				.toList();
		this.dependencies.addAll(references);

		this.type = clazz;
		this.provider = null;
		this.valueFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(InjectableField::new)
				.toList();
		this.afterInitMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, AfterInit.class))
				.toList();
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

	public boolean isGrainApplication() {
		return isAnnotationPresent(type, GrainApplication.class);
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

	public int getOrder() {
		return order;
	}

	@Override
	public boolean equals(Object o) {
		return o == this;
	}

	@Override
	public int compareTo(Injectable<T> o) {
		return getComparator()
				.compare(this, o);
	}

	public static Comparator<Injectable<?>> getComparator() {
		return Comparator.comparingInt((ToIntFunction<Injectable<?>>) Injectable::getOrder)
				.thenComparing(Injectable::getType, ReflectionUtil::compareLibraryAndUserPackage);
	}
}
