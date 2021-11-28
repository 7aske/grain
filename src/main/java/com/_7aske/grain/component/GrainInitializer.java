package com._7aske.grain.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.exception.GrainMultipleImplementationsException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	public GrainInitializer() {
		this.dependencies = new HashSet<>();
	}

	public Map<Class<?>, Object> initialize(Set<Class<?>> classes) {
		// We filter out all the dependencies that are already been added to the
		// dependency list
		this.dependencies.addAll(classes.stream()
				.filter(c -> this.dependencies.stream().noneMatch(d -> c.equals(d.clazz)))
				.map(Dependency::new).collect(Collectors.toList()));
		this.dependencies.forEach(this::initializeConstructors);
		this.dependencies.forEach(this::loadOwnDependencies);
		this.dependencies.forEach(this::partiallyInitialize);
		this.dependencies.forEach(this::initializeMissingFields);
		this.dependencies.forEach(this::callLifecycleMethods);
		return this.dependencies.stream().collect(Collectors.toMap(Dependency::getClazz, Dependency::getInstance));
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
		try {
			Dependency[] deps = mapParamsToDependencies(dependency, this.dependencies);
			// All dependencies should be satisfied before starting the initialization
			if (Arrays.stream(deps).anyMatch(Objects::isNull)) {
				throw new GrainDependencyUnsatisfiedException(dependency.clazz, dependency.params);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private void initializeMissingFields(Dependency dep) {
		Class<?> clazz = dep.clazz;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				// If the field is already initialized(some will be) we don't do
				// anything.
				if (field.get(dep.instance) != null) continue;
				// We do the initialization only for @Inject marked attributes or
				// for the attributes that appear as constructor parameters.
				if (isAnnotationPresent(field, Inject.class) || (isConstructorParam(dep.constructor, field.getType()))) {
					Dependency dependency = findDependencyByClass(field.getType())
							.orElseThrow(() -> new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s: %s", dep.clazz, field.getType())));
					partiallyInitialize(dependency);
					field.set(dep.instance, dependency.instance);
				}
			} catch (IllegalAccessException e) {
				throw new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s", dep.clazz), e);
			}
			field.setAccessible(false);
		}
	}

	// Is valid constructor parameter
	private boolean isConstructorParam(Constructor<?> constructor, Class<?> type) {
		return List.of(constructor.getParameterTypes()).contains(type) && isAnnotationPresent(type, Grain.class);
	}

	private Optional<Dependency> findDependencyByClass(Class<?> clazz) {
		List<Dependency> result = dependencies.stream()
				.filter(d -> d.clazz.equals(clazz) || clazz.isAssignableFrom(d.clazz))
				.collect(Collectors.toList());

		if (result.size() > 1) {
			// User defined dependencies are the ones that do not start
			// with grain library base package which is the package of
			// GrainApp.class.
			List<Dependency> userDefined = result.stream()
					.filter(dep -> {
						String basePackage = GrainApp.class.getPackageName();
						String depPackage = dep.clazz.getPackageName();

						// If the package is not starting with package but if it is make sure by checking whether the next
						// letter after the basePackage is a dot since in case of com._7aske.grain as basePackge and
						// com._7aske.graintest only by checking starts with would return true. This can be refactored
						// to match paths like we do it for url path matching.
						return !(depPackage.startsWith(basePackage) &&
								depPackage.charAt(basePackage.length()) == '.');
					})
					.collect(Collectors.toList());
			if (userDefined.size() > 1) {
				if (userDefined.stream().noneMatch(g -> isAnnotationPresent(g.getClass(), Primary.class))) {
					throw new GrainMultipleImplementationsException(clazz);
				} else {
					// @Incomplete Handle the case where use has defined
					// multiple @Primary grains
					return userDefined.stream()
							.filter(g -> isAnnotationPresent(g.getClass(), Primary.class))
							.findFirst();
				}
			} else if (userDefined.size() == 1) {
				return Optional.of(userDefined.get(0));
			}
		}

		if (result.isEmpty()) {
			return Optional.empty();
		}

		// in any other case just return the first found dependency
		return Optional.of(result.get(0));
	}

	// Creates a new instance of the dependency
	private void partiallyInitialize(Dependency dep) {
		if (dep.visited || dep.initialized) return;
		dep.visited = true;
		if (dep.instance == null) {
			try {
				Dependency[] deps = mapParamsToDependencies(dep.params, dependencies);

				Object[] realParams = new Object[dep.params.length];
				for (int i = 0; i < dep.params.length; i++) {
					partiallyInitialize(deps[i]);
					realParams[i] = deps[i].instance;
				}
				// create a new instance
				dep.instance = dep.constructor.newInstance(realParams);
				dep.initialized = true;
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new GrainInitializationException(String.format("Could not instantiate grain %s.", dep.clazz), e);
			}
		}
	}

	// @DeadCode
	private void callLifecycleMethods() {
		dependencies.forEach(this::callLifecycleMethods);
	}

	// @Note Lifecycle method parameters if any only be other Grains
	private void callLifecycleMethods(Dependency dependency) {
		for (Method method : dependency.instance.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(AfterInit.class)) {
				try {
					// We call the lifecycle methods with any other Grain instances as parameters
					method.invoke(dependency.instance, mapParamsToInstances(method.getParameterTypes(), dependencies));
				} catch (IllegalAccessException | InvocationTargetException e) {
					// @Incomplete should we throw
					e.printStackTrace();
				}
			}
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

	private Dependency[] mapParamsToDependencies(Dependency dependency, Set<Dependency> allDependencies) throws NoSuchMethodException {
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

		public Dependency(Class<?> clazz) {
			this.constructor = null;
			this.params = null;
			this.clazz = clazz;
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
