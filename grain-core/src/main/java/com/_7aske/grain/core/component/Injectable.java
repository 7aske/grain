package com._7aske.grain.core.component;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.core.configuration.GrainFertilizer;
import com._7aske.grain.core.reflect.ReflectionUtil;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.gtl.interpreter.Interpreter;
import com._7aske.grain.util.By;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com._7aske.grain.core.reflect.ReflectionUtil.isAnnotationPresent;

public class Injectable implements Ordered, Comparable<Injectable> {
	private final String name;
	private final Class<?> type;
	private Constructor<?> constructor;
	private final InjectableReference[] constructorParameters;
	private final List<Method> grainMethods;
	private final List<Field> injectableFields;
	private final List<InjectableReference> dependencies;
	private final List<InjectableField> valueFields;
	private final List<Method> afterInitMethods;
	private final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();
	private final int order;
	private boolean primary;
	/**
	 * Dependency that provides this dependency via the @Grain annotated method.
	 * Should be resolved and initialized before this dependency is initialized.
	 */
	private Injectable parent;
	private Method parentMethod;
	private Object instance;
	private final Conditional conditional;
	private final Annotation condition;

	private Injectable(Class<?> type, String name, Constructor<?> constructor, int order, Annotation condition) {
		this.name = name;
		this.type = type;
		this.parent = null;
		this.parentMethod = null;
		this.constructor = constructor;
		this.constructorParameters = new InjectableReference[0];
		this.dependencies = new ArrayList<>();
		this.grainMethods = new ArrayList<>();
		this.injectableFields = new ArrayList<>();
		this.valueFields = new ArrayList<>();
		this.afterInitMethods = new ArrayList<>();
		this.order = order;
		this.primary = isAnnotationPresent(type, Primary.class);
		this.condition = condition;
		this.conditional = condition != null
				? condition.annotationType().getAnnotation(Conditional.class)
				: null;
	}

	public static Injectable ofMethod(@NotNull Method method, @Nullable String name, Injectable provider) {
		int order = Optional.ofNullable(method.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);
		Class<?> type = method.getReturnType();
		Injectable injectable = new Injectable(
				type,
				name,
				null,
				order,
				ReflectionUtil.getAnnotatedBy(method, Conditional.class)
		);
		injectable.parent = provider;
		injectable.parentMethod = method;
		injectable.primary = isAnnotationPresent(method, Primary.class);
		Collection<InjectableReference> dependencies = Arrays.stream(method.getParameters())
				.map(InjectableReference::of)
				.toList();
		injectable.dependencies.addAll(dependencies);

		return injectable;
	}

	public static Injectable ofInitialized(Object grain) {
		return new Injectable(grain.getClass(), null, null, Order.HIGHEST_PRECEDENCE, null);
	}

	public Injectable(@NotNull Class<?> clazz, @Nullable String name) {
		this.type = clazz;
		this.name = name;
		this.parent = null;
		this.parentMethod = null;
		if (this.type.isInterface()) {
			this.constructor = null;
			this.constructorParameters = new InjectableReference[0];
		} else {
			try {
				this.constructor = ReflectionUtil.getBestConstructor(this.type);
				this.constructorParameters = Arrays.stream(this.constructor.getParameters())
						.map(InjectableReference::of)
						.toArray(InjectableReference[]::new);
			} catch (NoSuchMethodException e) {
				throw new GrainInitializationException("No constructor found for class " + this.type.getName());
			}
		}

		if (isAnnotationPresent(this.type, GrainFertilizer.class)) {
			this.order = Order.HIGHEST_PRECEDENCE;
		} else {
			this.order = Optional.ofNullable(this.type.getAnnotation(Order.class))
					.map(Order::value)
					.orElse(Order.DEFAULT);
		}
		this.primary = isAnnotationPresent(this.type, Primary.class);

		this.grainMethods = Arrays.stream(this.type.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, Grain.class))
				.sorted(By::order)
				.toList();

		this.injectableFields = Arrays.stream(this.type.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Inject.class))
				.toList();

		this.dependencies = new ArrayList<>();
		// Filter added to prevent the Injectable to depend on itself
		List<InjectableReference> references = Arrays.stream(this.constructorParameters)
				.filter(d -> !d.getType().isAssignableFrom(this.type))
				.toList();
		this.dependencies.addAll(references);

		this.valueFields = Arrays.stream(this.type.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Value.class))
				.map(InjectableField::new)
				.toList();
		this.afterInitMethods = Arrays.stream(this.type.getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, AfterInit.class))
				.sorted(By::order)
				.toList();
		this.condition = ReflectionUtil.getAnnotatedBy(this.type, Conditional.class);
		this.conditional = this.condition != null
				? condition.annotationType().getAnnotation(Conditional.class)
				: null;
	}

	public boolean evaluateCondition(DependencyContainer dependencyContainer, Interpreter interpreter) {
		if (this.conditional == null) {
			return true;
		}

		AbstractConditionEvaluator<? extends Annotation> evaluator = ReflectionUtil.newInstance(ReflectionUtil.getConstructor(this.conditional.value(), Class.class), this.condition.annotationType());

		return evaluator.doEvaluate(this.condition, this, dependencyContainer, interpreter);
	}

	public <T> T getInstance() {
		return (T) instance;
	}

	public <T> void setInstance(T instance) {
		this.instance = instance;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public void setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
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

	public boolean isInterface() {
		return type.isInterface();
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
		StringBuilder builder = new StringBuilder();
		builder.append(type.getSimpleName());

		String grainName = Optional.ofNullable(name)
				.map("\"%s\""::formatted)
				.orElse("");
		builder.append("(").append(grainName).append(")");

		if (isGrainMethodDependency()) {
			builder.append(" provided by ").append(parent.getType().getSimpleName());
			String parentName = parent.getName()
					.or(() -> Optional.of(grainNameResolver.resolveReferenceName(parent.getType())))
					.map("\"%s\""::formatted)
					.orElse("");
			builder.append("(").append(parentName).append(")");
		}

		builder.append("[").append(type.getPackageName()).append("]");
		builder.append("[").append(order).append("]");


		return builder.toString();
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
		if (primary && !o.primary) return -1;
		if (!primary && o.primary) return 1;

		return By.<Injectable>order()
				.thenComparing(By.packages(Injectable::getType))
				.compare(this, o);
	}

	public boolean hasGrainMethodDependencies() {
		return !grainMethods.isEmpty();
	}

	public Method getParentMethod() {
		return parentMethod;
	}
}
