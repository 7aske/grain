package com._7aske.grain.core.component;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.exception.GrainInvalidInjectException;
import com._7aske.grain.exception.GrainReflectionException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.*;

/**
 * Class responsible for initializing all components registered for
 * dependency injection.
 */
public class GrainInitializer {
	// Set of parsed dependencies
	private final Set<Dependency> dependencies;
	private final Interpreter interpreter;

	public GrainInitializer(Configuration configuration) {
		this.dependencies = new HashSet<>();
		this.interpreter = new Interpreter();
		interpreter.putProperties(configuration.getProperties());
	}

	public Map<Class<?>, Object> initialize(Set<Class<?>> classes) {
		// We filter out all the dependencies that are already been added to the
		// dependency list
		this.dependencies.addAll(classes.stream()
				.filter(c -> this.dependencies.stream().noneMatch(d -> c.equals(d.clazz)))
				.map(Dependency::new).collect(Collectors.toList()));
		this.dependencies.forEach(this::initializeConstructors);
		this.dependencies.forEach(this::loadOwnDependencies);
		// We need a copy of the set to prevent ConcurrentModificationExceptions
		new HashSet<>(this.dependencies).forEach(this::partiallyInitialize);
		this.dependencies.forEach(this::initializeMissingFields);

		// @Temporary
		// Before running code segments in @Value annotations we need to load
		// the interpreter with initialized Grains.
		HashMap<String, Object> tmp = new HashMap<>();
		for (Dependency dependency : this.dependencies) {
			String clazzName = dependency.getClazz().getSimpleName();
			Grain grain = dependency.getClazz().getAnnotation(Grain.class);
			String grainName;
			// If the grain annotation has a valid 'name' property we use that
			// as the key for the given grain. Otherwise, we use lower-case
			// class name.
			if (grain == null || grain.name().isBlank()) {
				grainName = clazzName.substring(0, 1).toLowerCase(Locale.ROOT) + clazzName.substring(1);
			} else {
				grainName = grain.name();
			}
			tmp.put(grainName, dependency.instance);
		}
		interpreter.putSymbols(tmp);

		// @Refactor Disgusting double call of initializeValues. In the first
		// pass we initialize only the dependency values which has code that
		// references exclusively values from loaded from the configuration and
		// not from other Grains. After we initialized everything that we've
		// skipped in HOPES (sorry Bryan) that we've initialized everything.
		this.dependencies.forEach(this::initializeValues);
		pass++;
		this.dependencies.forEach(this::initializeValues);
		this.dependencies.forEach(this::callLifecycleMethods);
		return this.dependencies.stream().collect(Collectors.toMap(Dependency::getClazz, Dependency::getInstance));
	}

	// @Hack @Temporary there should be better way to determine which fields
	// to initialize instead of a pass counter.
	private int pass = 1;

	// We do this pass after initializing all the fields since fields can
	// hopefully reference other Grains
	private void initializeValues(Dependency dep) {
		for (DependencyField field : dep.fields) {
			try {

				if (field.isInitialized()) continue;

				if (field.isAnnotationPresent(Inject.class))
					throw new GrainInvalidInjectException("Cannot mark field as @Inject and @Value");
				Value value = field.getAnnotation(Value.class);
				String code = value.value();
				// We're analyzing the code to find Values that are referencing
				// only values from properties to initialize them first.
				boolean onlyProperties = interpreter.analyze(code);

				// First pass initializes Values that are referencing only
				// props. Second pass initializes everything else.
				if (onlyProperties || pass == 2) {
					field.setInitialized(true);

					// We cast the result to confirm that we have a valid value
					Object result = field.getType().cast(interpreter.evaluate(code));
					ReflectionUtil.setFieldValue(field.get(), dep.instance, result);
				}

			} catch (GrainReflectionException e) {
				throw new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s", dep.clazz), e);
			}
		}
	}

	public Object initialize(Class<?> clazz) {
		Dependency dependency = new Dependency(clazz);
		initializeConstructors(dependency);
		loadOwnDependencies(dependency);
		// create a new instance
		partiallyInitialize(dependency);
		// another pass to initialize fields that are possibly
		// subjects to circular dependencies
		initializeMissingFields(dependency);
		initializeValues(dependency);
		callLifecycleMethods(dependency);
		dependencies.add(dependency);
		return dependency.instance;
	}


	// Add already initialized object as a dependency. This is useful when
	// we want to add objects that are unable to participate in the dependency
	// injection mechanism. E.g. Configuration.
	public Object addInitialized(Object object) {
		Dependency dependency = new Dependency(object.getClass());
		dependency.instance = object;
		dependency.visited = true;
		dependency.initialized = true;
		this.dependencies.add(dependency);
		return object;
	}

	// @Bug this fails with interface Grains with default method implementations
	private void initializeConstructors(Dependency dependency) {
		try {
			// If the class is an interface we find the first dependency which
			// implements the interface in question.
			if (dependency.clazz.isInterface()) {
				Dependency resolved = dependencies.stream()
						.filter(dep -> {
							if (dep.clazz.equals(dependency.clazz))
								return false;
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
			// @Incomplete should we throw?
			e.printStackTrace();
		}
	}

	// Loads dependencies for a dependency according to its constructor parameter
	// types.
	private void loadOwnDependencies(Dependency dependency) {
		if (dependency.initialized) return;

		Dependency[] deps = mapParamsToDependencies(dependency, this.dependencies);
		// All dependencies should be satisfied before starting the initialization
		if (Arrays.stream(deps).anyMatch(Objects::isNull)) {
			throw new GrainDependencyUnsatisfiedException(dependency.clazz, dependency.params);
		}
	}

	private void initializeMissingFields(Dependency dep) {
		Class<?> clazz = dep.clazz;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			try {

				// If the field is already initialized(some will be) we don't do
				// anything.
				if (getFieldValue(field, dep.instance) != null) continue;
				// We do the initialization only for @Inject marked attributes or
				// for the attributes that appear as constructor parameters.
				if (isAnnotationPresent(field, Inject.class) || (isConstructorParam(dep.constructor, field.getType()))) {
					if (Iterable.class.isAssignableFrom(field.getType())) {
						Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
						List<Object> instances = dependencies.stream()
								.filter(d -> genericType.isAssignableFrom(d.clazz))
								.map(d -> d.instance)
								.collect(Collectors.toList());
						ReflectionUtil.setFieldValue(field, dep.instance, instances);
					} else {
						Dependency dependency = findDependencyByClass(field.getType())
								.orElseThrow(() -> new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s: %s", dep.clazz, field.getType())));
						partiallyInitialize(dependency);
						ReflectionUtil.setFieldValue(field, dep.instance, dependency.instance);
					}
				}
			} catch (GrainReflectionException e) {
				throw new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s", dep.clazz), e);
			}
		}
	}

	// Is valid constructor parameter
	private boolean isConstructorParam(Constructor<?> constructor, Class<?> type) {
		return List.of(constructor.getParameterTypes()).contains(type) && isAnnotationPresent(type, Grain.class);
	}

	private Optional<Dependency> findDependencyByClass(Class<?> clazz) {
		return findClassByClass(clazz, dependencies, d -> d.clazz);
	}

	// Creates a new instance of the dependency
	private void partiallyInitialize(Dependency dep) {
		if (dep.visited || dep.initialized) return;
		dep.visited = true;
		if (dep.instance == null) {
			Dependency[] deps = mapParamsToDependencies(dep.params, dependencies);

			Object[] realParams = new Object[dep.params.length];
			for (int i = 0; i < dep.params.length; i++) {
				partiallyInitialize(deps[i]);
				realParams[i] = deps[i].instance;
			}
			// create a new instance
			dep.instance = newInstance(dep.constructor, realParams)
					.orElseThrow(() -> new GrainInitializationException(String.format("Could not instantiate grain %s.", dep.clazz)));
			dep.initialized = true;

			// Then, we call Grain methods
			List<Method> methods = Arrays.stream(dep.getClazz().getDeclaredMethods())
					.filter(m -> m.isAnnotationPresent(Grain.class))
					.collect(Collectors.toList());

			// @Refactor
			for (Method method : methods) {
				Object result = ReflectionUtil.invokeMethod(method, dep.instance, mapParamsToInstances(method.getParameterTypes(), dependencies));
				// @Note This can be either method#getReturnType() or result#getClass();
				Dependency dependency = new Dependency(method.getReturnType());
				dependency.initialized = true;
				dependency.visited = true;
				dependency.instance = result;
				// @Todo Handle multiple declarations of the same bean.
				// Consider using @Primary annotation.
				dependencies.add(dependency);
			}
		}
	}

	// @DeadCode
	private void callLifecycleMethods() {
		dependencies.forEach(this::callLifecycleMethods);
	}

	// @Note Lifecycle method parameters if any only be other Grains
	private void callLifecycleMethods(Dependency dependency) {
		if (!dependency.lifecycleMethodCalled) {
			for (Method method : dependency.instance.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(AfterInit.class)) {
					// We call the lifecycle methods with any other Grain instances as parameters
					ReflectionUtil.invokeMethod(method, dependency.instance, mapParamsToInstances(method.getParameterTypes(), dependencies));
				}
			}
			dependency.lifecycleMethodCalled = true;
		}
	}

	private Object[] mapParamsToInstances(Class<?>[] params, Set<Dependency> allDependencies) {
		return Arrays.stream(mapParamsToDependencies(params, allDependencies)).map(d -> d.instance).toArray(Object[]::new);
	}

	private Dependency[] mapParamsToDependencies(Class<?>[] params, Set<Dependency> allDependencies) {
		return Arrays.stream(params)
				.map(this::findDependencyByClass)
				.map(o -> o.orElse(null))
				.toArray(Dependency[]::new);
	}

	private Dependency[] mapParamsToDependencies(Dependency dependency, Set<Dependency> allDependencies) {
		return mapParamsToDependencies(dependency.constructor.getParameterTypes(), allDependencies);
	}

	// Class representing a dependency participating in the dependency injection
	// mechanism.
	private static final class Dependency {
		private boolean visited;
		private boolean initialized;
		private final Class<?> clazz;
		private Constructor<?> constructor;
		private Class<?>[] params;
		private Object instance;
		private boolean lifecycleMethodCalled;
		private final List<DependencyField> fields;

		public Dependency(Class<?> clazz) {
			this.constructor = null;
			this.params = null;
			this.clazz = clazz;
			this.visited = false;
			this.instance = null;
			this.initialized = false; // for manually initialized grains
			this.lifecycleMethodCalled = false;
			this.fields = Arrays.stream(clazz.getDeclaredFields())
					.filter(f -> isAnnotationPresent(f, Value.class))
					.map(DependencyField::new)
					.collect(Collectors.toList());
		}

		public List<DependencyField> getFields() {
			return fields;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public Object getInstance() {
			return instance;
		}
	}


	public static class DependencyField {
		private final Field field;
		private boolean initialized;

		public DependencyField(Field field) {
			this.field = field;
			this.initialized = false;
		}

		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return field.getAnnotation(annotationClass);
		}

		public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
			return field.isAnnotationPresent(annotationClass);
		}

		public Class<?> getType() {
			return field.getType();
		}

		public Field get() {
			return field;
		}

		public boolean isInitialized() {
			return initialized;
		}

		public void setInitialized(boolean initialized) {
			this.initialized = initialized;
		}
	}
}
