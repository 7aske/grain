package com._7aske.grain.core.component;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.exception.GrainInvalidInjectException;
import com._7aske.grain.exception.GrainReflectionException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.core.reflect.GrainProxyFactory;
import com._7aske.grain.core.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.core.reflect.ReflectionUtil.isAnnotationPresent;

/**
 * Injector class that is the basis of Dependency Injection in Grain framework.
 * Injector initializes all provided dependencies and stores them in a {@link DependencyContainer}.
 */
public class GrainInjector {
	private final DependencyContainerImpl container;
	private final Interpreter interpreter;
	private final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();
	private final GrainProxyFactory grainProxyFactory;
	private final Logger logger = LoggerFactory.getLogger(GrainInjector.class);

	public GrainInjector(Configuration configuration) {
		this.container = new DependencyContainerImpl();
		this.interpreter = new Interpreter();
		this.grainProxyFactory = new GrainProxyFactory(container, grainNameResolver);
		interpreter.putProperties(configuration.getProperties());
		interpreter.putSymbol("configuration", configuration);
		inject(this);
		inject(configuration);
		inject(container);
	}

	public DependencyContainer getContainer() {
		return container;
	}

	/**
	 * Injects the already initialized object.
	 *
	 * @param object the object to inject
	 */
	public void inject(Object object) {
		inject(object.getClass(), object);
	}

	/**
	 * Injects the already initialized object. To be used with proxied objects.
	 *
	 * @param clazz Type of the object
	 * @param object Object to inject
	 */
	public void inject(Class<?> clazz, Object object) {
		if (!isAnnotationPresent(clazz, Grain.class)) {
			logger.warn("Registered Grain {} without @Grain annotation", object.getClass());
		}
		Injectable injectable = new Injectable(
				clazz,
				grainNameResolver.resolveDeclarationName(object.getClass()));
		injectable.setObjectInstance(object);
		container.add(injectable);
	}

	/**
	 * Injects and initializes a single class.
	 *
	 * @param clazz the class to inject and initialize
	 */
	public void inject(Class<?> clazz) {
		if (!isAnnotationPresent(clazz, Grain.class)) {
			logger.warn("Registered Grain {} without @Grain annotation", clazz);
		}

		Injectable injectable = new Injectable(
				clazz,
				grainNameResolver.resolveDeclarationName(clazz));
		initialize(injectable);
		container.add(injectable);
	}

	/**
	 * Initializes and "injects" the given set of classes.
	 *
	 * @param classes The classes to initialize.
	 */
	public void inject(Set<Class<?>> classes) {
		// First, go through all the classes converting them into dependencies
		for (Class<?> clazz : classes) {
			if (!checkCondition(clazz.getAnnotation(Condition.class))) {
				continue;
			}

			Injectable dependency = new Injectable(
					clazz,
					grainNameResolver.resolveDeclarationName(clazz)
			);
			// One thing that cannot be done inside the BetterDependency constructor
			// because resulting dependencies need to be added to the DependencyContainer.
			dependency.getGrainMethods().forEach(method -> {
				// In situations where we have grains resulting from grain methods
				// we first must initialize the grain that is providing the method.
				// Because of that we set the "provider" field as a reference if
				// we happen to come across grain method dependencies before their
				// providers in the injection pipeline.
				Injectable methodDependency = Injectable.ofMethod(
						method,
						grainNameResolver.resolveReferenceName(method),
						dependency);

				// If the Grain method returns the same type (or subtype) as one of the
				// parameters in the Grain method parameter list we consider that
				// as an "Override" injection, and therefore we do not need to
				// create a new dependency in the container for it.
				// Example would be a Grain method that is used to update the
				// Configuration class:
				// @Grain
				// public Configuration configuration(Configuration config) {
				//     config.setSomething("something");
				//     return config;
				// }
				Optional<Injectable> grain = this.container.getByClass(method.getReturnType());
				if (grain.isPresent()) {
					boolean isSelfReferencing = Arrays.stream(method.getParameterTypes())
							.anyMatch(p -> method.getReturnType().isAssignableFrom(p));
					if (isSelfReferencing) {
						return;
					}
				}

				// We add the dependency to the DependencyContainer allowing
				// other grains to use it.
				if (checkCondition(method.getAnnotation(Condition.class))) {
					this.container.add(methodDependency);
				}
			});
			this.container.add(dependency);
		}

		// Second, after resolving all dependencies we check whether there
		// is any circular dependencies between grains. If yes we need to
		// throw an exception.
		logger.debug("Checking for circular dependencies");
		checkCircularDependencies();

		// Third, we initialize all dependencies and set their instances.
		logger.debug("Initializing dependencies");
		for (Injectable dependency : this.container) {
			// These should be skipped as they are added to the dependency
			// container but are not actual classes that we should initialize
			// in the DI process. Rather we let grain methods do that.
            if (dependency.isGrainMethodDependency()) {
                continue;
            }

            initialize(dependency);
        }

		// @Temporary
		// Before running code segments in @Value annotations we need to load
		// the interpreter with initialized Grains.
		HashMap<String, Object> tmp = new HashMap<>();
		for (Injectable dependency : this.container) {
			tmp.put(grainNameResolver.resolveReferenceName(dependency.getType()), dependency.getInstance());
		}
		interpreter.putSymbols(tmp);

		// Fourth, we evaluate @Value annotations on all dependencies.
		logger.debug("Evaluating @Value annotations");
		for (Injectable dependency : this.container) {
			evaluateValueAnnotations(dependency);
		}

		// Finally, we call lifecycle methods on all dependencies.
		logger.debug("Calling lifecycle methods");
		for (Injectable dependency : this.container) {
			for (Method method : dependency.getAfterInitMethods()) {
				ReflectionUtil.invokeMethod(method, dependency.getInstance(), mapMethodParametersToDependencies(method));
			}
		}

		logger.debug("Loaded {} Grain classes", container.getAll().size());
	}

	/**
	 * Evaluates @Condition annotation to determine whether the given class
	 * should be initialized.
	 *
	 * @param condition The @Condition annotation to evaluate.
	 * @return True if the class should be initialized.
	 */
	private boolean checkCondition(Condition condition) {
		if (condition == null || condition.value() == null || condition.value().isBlank()) {
			return true;
		}

		String code = condition.value();
		return Boolean.parseBoolean(String.valueOf(interpreter.evaluate(code)));
	}

	/**
	 * Evaluates @Value annotations on the given dependency.
	 *
	 * @param dependency The dependency to evaluate @Value annotations on.
	 */
	private void evaluateValueAnnotations(Injectable dependency) {
		for (InjectableField field : dependency.getValueFields()) {
			try {
				// We don't allow both annotations because @Value will overwrite
				// the dependency injected by @Inject. In practice doesn't make
				// much sense.
				if (field.isAnnotationPresent(Inject.class))
					throw new GrainInvalidInjectException("Cannot mark field as @Inject and @Value");
				Value value = field.getAnnotation(Value.class);
				String code = value.value();

				// We cast the result to confirm that we have a valid value
				Object result = field.getType().cast(interpreter.evaluate(code));
				ReflectionUtil.setFieldValue(field.get(), dependency.getInstance(), result);

			} catch (GrainReflectionException e) {
				throw new GrainDependencyUnsatisfiedException(String.format("Grain dependencies unsatisfied for class %s", dependency.getType().getName()), e);
			}
		}
	}

	/**
	 * Initializes a dependency by instantiating it and setting its instance.
	 * If needed it initializes the dependencies of the dependency. Also,
	 * calls @Grain methods and initializes returned dependencies.
	 *
	 * @param dependency The dependency to initialize.
	 */
	private void initialize(Injectable dependency) {
		// If the dependency is initialized already we do nothing.
		if (dependency.isInitialized()) return;


		// We initialize the injectable
		Object instance;
		try {
			if (dependency.hasGrainMethodDependencies()) {
				// If the dependency has grain method dependencies we need to
				// create a proxy for it and delegate all method calls to the
				// GrainResolvingProxyInterceptor in order to prevent creation
				// of multiple instances of the same method dependency.
				// E.g. if we have a Grain method that returns a Configuration,
				// and we call it in multiple places we will have multiple instances
				// of the Configuration class.
				instance = grainProxyFactory.create(
						dependency.getType(),
						dependency.getConstructor().getParameterTypes(),
						mapConstructorParametersToDependencies(dependency));
			} else if (dependency.isGrainMethodDependency()) {
				instance = InjectableReference.of(dependency.getParentMethod())
						.resolve(container);
			} else {
				instance = ReflectionUtil.newInstance(
						dependency.getConstructor(),
						mapConstructorParametersToDependencies(dependency));
			}
		} catch (GrainReflectionException e) {
			throw new GrainInitializationException(String.format("Unable to instantiate grain %s", dependency), e);
		}

		dependency.setInstance(instance);
		logger.debug("Initialized '{}'", dependency.getType().getName());

		// After initialization, we call @Grain methods and update the appropriate
		// injectable with the result
        for (Method method : dependency.getGrainMethods()) {
			try {
				resolveGrainMethod(method, instance);
			} catch (GrainReflectionException e) {
				throw new GrainInitializationException("Failed to initialize grain " + dependency, e);
			}
        }


        // Finally, we set values to all @Inject annotated fields.
        for (Field field : dependency.getInjectableFields()) {
            if (!checkCondition(field.getAnnotation(Condition.class))) {
                continue;
            }
            InjectableReference reference = InjectableReference.of(field);
            // @Note #resolveInstance will attempt to initialize the value if
            // it is not initialized but that should matter in this because it
            // already should've been initialized by now.
            Object value = resolveInstance(reference);
            ReflectionUtil.setFieldValue(field, instance, value);
        }
    }

	private Object resolveGrainMethod(Method method, Object instance) throws GrainReflectionException {
		Injectable methodDependency = InjectableReference.of(method)
				.resolve(container);
		Object result = ReflectionUtil.invokeMethod(method, instance, mapMethodParametersToDependencies(method));
		methodDependency.setInstance(result);

		return result;
	}

	/**
	 * Maps the method parameters to instances of corresponding dependencies.
	 *
	 * @param method The method to map parameters for.
	 * @return The list objects to be used as method parameters.
	 */
	private Object[] mapMethodParametersToDependencies(Method method) {
		Parameter[] parameters = method.getParameters();
		Object[] methodParameters = new Object[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			// If it is annotated with @Value we evaluate it and set its return
			// value as the parameter value
			if (isAnnotationPresent(parameters[i], Value.class)) {
				Value value = parameters[i].getAnnotation(Value.class);
				String code = value.value();

				// We cast the result to confirm that we have a valid value
				Object result = parameters[i].getType().cast(interpreter.evaluate(code));
				methodParameters[i] = result;
			} else {
				// Here we could check whether the parameter is annotated with
				// @Inject, but we assume that it is - rather we say that it is
				// implicitly annotated.
				methodParameters[i] = resolveInstance(InjectableReference.of(parameters[i]));
			}
		}
		return methodParameters;
	}

	/**
	 * Maps the constructor parameters of the injectable to instances of
	 * corresponding injectables.
	 *
	 * @param injectable The injectable to map parameters for.
	 * @return The list objects to be used as constructor parameters.
	 */
	private Object[] mapConstructorParametersToDependencies(Injectable injectable) {
		InjectableReference[] constructorParameters = injectable.getConstructorParameters();
		Object[] parameterInstances = new Object[constructorParameters.length];
		for (int i = 0; i < constructorParameters.length; i++) {
			InjectableReference injectableReference = constructorParameters[i];
			parameterInstances[i] = resolveInstance(injectableReference);
		}
		return parameterInstances;
	}

	/**
	 * Resolves the injectable reference to an object instance. Takes into the
	 * account the type of the reference.
	 *
	 * @param injectableReference The injectable reference to resolve.
	 * @return The object instance.
	 */
	private Object resolveInstance(InjectableReference injectableReference) {
		// @Todo add the same logic for Optional
		if (injectableReference.isCollection()) {
			return injectableReference.resolveList(this.container)
					.stream()
					.map(dep -> {
						// We need to initialize the provider if the dependency has
						// one, and it is not initialized yet, i.e. we cannot
						// call a method on an uninitialized dependency.
						if (dep.isGrainMethodDependency() && !dep.getParent().isInitialized()) {
							initialize(dep.getParent());
						}
						if (!dep.isInitialized()) {
							initialize(dep);
						}
						return dep;
					})
					.map(Injectable::getInstance)
					.toList();
		} else {
			Injectable dependency = injectableReference
					.resolve(this.container);
			// Dependency was not required and not found
			if (dependency == null) {
				logger.warn("Non required dependency '{}' not found", injectableReference.getName());
				return null;
			}
			// We need to initialize the provider if the dependency has
			// one, and it is not initialized yet, i.e. we cannot
			// call a method on an uninitialized dependency.
			if (dependency.isGrainMethodDependency() && !dependency.getParent().isInitialized()) {
				initialize(dependency.getParent());
			}

			if (!dependency.isInitialized()) {
				initialize(dependency);
			}

			return dependency.getInstance();
		}
	}

	/**
	 * Method to check whether any of the dependencies form a circle. If that
	 * happens we must instruct the user to re-organize the dependencies and
	 * prevent the dependency circle from being created.
	 */
	private void checkCircularDependencies() {
		Collection<Injectable> checked = new HashSet<>();
		for (Injectable dependency : container) {
			check(dependency, dependency.getDependencies(), new ArrayList<>(), checked);
		}
		logger.info("Checked {} dependencies for circular dependencies", checked.size());
	}

	/**
	 * Method to be called recursively until all dependencies are exhausted or
	 * a circular dependency is found.
	 *
	 * @param start The dependency to start with.
	 * @param dependencies The dependencies to check.
	 * @param steps The steps taken so far.
	 * @param checked The dependencies that were checked.
	 */
	private void check(Injectable start, List<InjectableReference> dependencies, List<InjectableReference> steps, Collection<Injectable> checked) {
		if (checked.contains(start)) {
			return;
		}


		for (InjectableReference dependency : dependencies) {
			steps.add(dependency);
			Collection<Injectable> dependencyDependencies = dependency.resolveList(this.container);

			if (dependencyDependencies.stream().anyMatch(d -> Objects.equals(d, start))) {
				throw new GrainInitializationException("Circular dependency detected:\n\n" + start + "\n\t|\n\tv\n" + steps.stream().map(InjectableReference::toString).collect(Collectors.joining("\n\t|\n\tv\n")) + "\n");
			}

			check(start, dependencyDependencies.stream().flatMap(d -> d.getDependencies().stream()).toList(), steps, checked);
		}

		checked.add(start);
	}
}
