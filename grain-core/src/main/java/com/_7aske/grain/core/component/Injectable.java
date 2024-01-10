package com._7aske.grain.core.component;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.By;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

class Injectable implements Ordered, Comparable<Injectable> {
	private final String name;
	private final Class<?> type;
	private final Constructor<?> constructor;
	private final InjectableReference[] constructorParameters;
	private final List<Method> grainMethods;
	private final List<Field> injectableFields;
	private final List<InjectableReference> dependencies;
	private final List<InjectableField> valueFields;
	private final List<Method> afterInitMethods;
	private final int order;
	/**
	 * Dependency that provides this dependency via the @Grain annotated method.
	 * Should be resolved and initialized before this dependency is initialized.
	 */
	private Injectable parent;
	private Object instance;

	private Injectable(Class<?> type, String name, Constructor<?> constructor, int order) {
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

	public static Injectable ofMethod(@NotNull Method method, @Nullable String name, Injectable provider) {
		int order = Optional.ofNullable(method.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);
		Injectable injectable = new Injectable(method.getReturnType(), name, null, order);
		injectable.setParent(provider);
		return injectable;
	}

	public static  Injectable ofInitialized(Object grain) {
		return new Injectable(grain.getClass(), null, null, Order.HIGHEST_PRECEDENCE);
	}

	public Injectable(@NotNull Class<?> clazz, @Nullable String name) {
		this.name = name;
		if (clazz.isInterface()) {
			this.constructor = null;
			this.constructorParameters = new InjectableReference[0];
			this.instance = ReflectionUtil.createProxy(clazz);
		} else {
			try {
				this.constructor = ReflectionUtil.getBestConstructor(clazz);
//				if (this.constructor == null) {
//					throw new GrainInitializationException("No constructor found for class " + clazz.getName());
//				}
				this.constructorParameters = Arrays.stream(this.constructor.getParameters())
						.map(InjectableReference::of)
						.toArray(InjectableReference[]::new);
			} catch (NoSuchMethodException e) {
				throw new GrainInitializationException("No constructor found for class " + clazz.getName());
			}
		}

		this.order = Optional.ofNullable(clazz.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);


		this.grainMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, Grain.class))
				.sorted(By::order)
				.toList();

		this.injectableFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Inject.class))
				.toList();

		List<InjectableReference> constructorDependencies = Arrays.asList(this.constructorParameters);

		this.dependencies = new ArrayList<>();
		// Filter added to prevent the Injectable to depend on itself
		List<InjectableReference> references = constructorDependencies.stream()
				.filter(d -> !d.getType().isAssignableFrom(clazz))
				.toList();
		this.dependencies.addAll(references);

		this.type = clazz;
		this.parent = null;
		this.valueFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(InjectableField::new)
				.toList();
		this.afterInitMethods = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, AfterInit.class))
				.sorted(By::order)
				.toList();
	}

	public <T> T getInstance() {
		return (T) instance;
	}

	public <T> void setInstance(T instance) {
		this.instance = instance;
	}

	public void setObjectInstance(Object instance) {
		this.instance = type.cast(instance);
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public InjectableReference[] getConstructorParameters() {
		return constructorParameters;
	}

	public List<InjectableReference> getDependencies() {
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
		return parent != null;
	}

	public Injectable getParent() {
		return parent;
	}

	public void setParent(Injectable parent) {
		this.parent = parent;
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
	public int getOrder() {
		return order;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		Injectable that = (Injectable) object;
		return Objects.equals(name, that.name) && Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}

	@Override
	public int compareTo(Injectable o) {
		return By.<Injectable>order()
				.thenComparing(By.packages(Injectable::getType))
				.compare(this, o);
	}
}
