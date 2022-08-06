package com._7aske.grain.core.component;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.exception.GrainInvalidInjectException;
import com._7aske.grain.exception.GrainReflectionException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class BetterGrainInitializer {
	private final DependencyContainer dependencies;
	private final Interpreter interpreter;
	private final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();

	public BetterGrainInitializer(Configuration configuration) {
		this.dependencies = new DependencyContainer();
		this.interpreter = new Interpreter();
		interpreter.putProperties(configuration.getProperties());
		interpreter.putSymbol("configuration", configuration);
	}

	public DependencyContainer getContainer() {
		return dependencies;
	}

	public DependencyContainer inject(Set<Class<?>> classes) {
		// First, go through all the classes converting them into dependencies
		for (Class<?> clazz : classes) {
			BetterDependency dependency = new BetterDependency(
					clazz,
					grainNameResolver.resolveReferenceName(clazz)
			);
			// One thing that cannot be done inside the BetterDependency constructor
			// because resulting dependencies need to be added to the DependencyContainer.
			dependency.getGrainMethods().forEach(method -> {
				// In situations where we have grains resulting from grain methods
				// we first must initialize the grain that is providing the method.
				// Because of that we set the "provider" field as a reference if
				// we happen to come across grain method dependencies before their
				// providers in the injection pipeline.
				BetterDependency methodDependency = BetterDependency.ofMethod(
						method.getReturnType(),
						grainNameResolver.resolveReferenceName(method),
						dependency);
				// We add the dependency to the DependencyContainer allowing
				// other grains to use it.
				this.dependencies.add(methodDependency);
			});
			this.dependencies.add(dependency);
		}

		// Second, after resolving all dependencies we check whether there
		// is any circular dependencies between grains. If yes we need to
		// throw an exception.
		checkCircularDependencies();

		// Third, we initialize all dependencies and set their instances.
		for (BetterDependency dependency : this.dependencies) {
			// These should be skipped as they are added to the dependency
			// container but are not actual classes that we should initialize
			// in the DI process. Rather we let grain methods do that.
			if (!dependency.isGrainMethodDependency()) {
				initialize(dependency);
			}
		}

		// @Temporary
		// Before running code segments in @Value annotations we need to load
		// the interpreter with initialized Grains.
		HashMap<String, Object> tmp = new HashMap<>();
		for (BetterDependency dependency : this.dependencies) {
			tmp.put(dependency.getName().get(), dependency.getInstance());
		}
		interpreter.putSymbols(tmp);

		// Fourth, we evaluate @Value annotations on all dependencies.
		for (BetterDependency dependency : this.dependencies) {
			evaluateValueAnnotations(dependency);
		}

		// Finally, we call lifecycle methods on all dependencies.
		for (BetterDependency dependency : this.dependencies) {
			for (Method method : dependency.getAfterInitMethods()) {
				ReflectionUtil.invokeMethod(method, dependency.getInstance(), mapMethodParametersToDependencies(method));
			}
		}

		return dependencies;
	}

	private void evaluateValueAnnotations(BetterDependency dependency) {
		for (BetterDependencyField field : dependency.getValueFields()) {
			try {
				if (field.isAnnotationPresent(Inject.class))
					throw new GrainInvalidInjectException("Cannot mark field as @Inject and @Value");
				Value value = field.getAnnotation(Value.class);
				String code = value.value();

				// We cast the result to confirm that we have a valid value
				Object result = field.getType().cast(interpreter.evaluate(code));
				ReflectionUtil.setFieldValue(field.get(), dependency.getInstance(), result);
				field.setInitialized(true);

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
	private void initialize(BetterDependency dependency) {
		// If the dependency is initialized already we do nothing.
		if (dependency.isInitialized()) return;

		Constructor<?> constructor = dependency.getConstructor();
		Object[] constructorParameters = mapConstructorParametersToDependencies(dependency);
		Object instance = ReflectionUtil.newInstance(constructor, constructorParameters)
				.orElseThrow(() -> new GrainInitializationException("Could not instantiate '" + dependency.getType() + "'"));
		dependency.setInstance(instance);

		dependency.getGrainMethods().forEach(method -> {
			try {
				BetterDependency methodDependency = DependencyReference.of(method)
						.resolve(dependencies);
				Object result = ReflectionUtil.invokeMethod(method, instance, mapMethodParametersToDependencies(method));
				methodDependency.setInstance(result);
			} catch (Exception e) {
				throw new GrainInitializationException("Failed to initialize grain " + dependency, e);
			}
		});

		// Finally, we set values to all @Inject annotated fields.
		dependency.getInjectableFields().forEach(field -> {
			DependencyReference reference = DependencyReference.of(field);
			// @Note #resolveInstance will attempt to initialize the value if
			// it is not initialized but that should matter in this because it
			// already should've been initialized by now.
			Object value = resolveInstance(reference);
			ReflectionUtil.setFieldValue(field, instance, value);
		});
	}

	private Object[] mapMethodParametersToDependencies(Method method) {
		Parameter[] parameters = method.getParameters();
		Object[] methodParameters = new Object[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (ReflectionUtil.isAnnotationPresent(parameters[i], Value.class)) {
				Value value = parameters[i].getAnnotation(Value.class);
				String code = value.value();

				// We cast the result to confirm that we have a valid value
				Object result = parameters[i].getType().cast(interpreter.evaluate(code));
				methodParameters[i] = result;
			} else {
				methodParameters[i] = resolveInstance(DependencyReference.of(parameters[i]));
			}
		}
		return methodParameters;
	}

	private Object[] mapConstructorParametersToDependencies(BetterDependency betterDependency) {
		DependencyReference[] constructorParameters = betterDependency.getConstructorParameters();
		Object[] parameterInstances = new Object[constructorParameters.length];
		for (int i = 0; i < constructorParameters.length; i++) {
			DependencyReference dependencyReference = constructorParameters[i];
			parameterInstances[i] = resolveInstance(dependencyReference);
		}
		return parameterInstances;
	}

	private Object resolveInstance(DependencyReference dependencyReference) {
		if (dependencyReference.isCollection()) {
			return dependencyReference.resolveList(this.dependencies)
					.stream()
					.peek(dep -> {
						if (dep.isGrainMethodDependency() && !dep.getProvider().isInitialized()) {
							initialize(dep.getProvider());
						}
						if (!dep.isInitialized()) {
							initialize(dep);
						}
					})
					.map(BetterDependency::getInstance)
					.collect(Collectors.toList());
		} else {
			BetterDependency dependency = dependencyReference
					.resolve(this.dependencies);
			if (dependency.isGrainMethodDependency() && !dependency.getProvider().isInitialized()) {
				initialize(dependency.getProvider());
			}
			if (!dependency.isInitialized())
				initialize(dependency);
			return dependency.getInstance();
		}
	}

	public void checkCircularDependencies() {
		for (BetterDependency dependency : dependencies.getAll()) {
			// We don't want to resolve dependencies defined by @Grain methods
			// because they are not defined as classes that should be a part
			// of dependency injection.
			if (!dependency.isGrainMethodDependency()) {
				check(dependency, dependency.getDependencies(), new ArrayList<>());
			}
		}
	}

	private void check(BetterDependency start, List<DependencyReference> dependencies, List<DependencyReference> steps) {
		for (DependencyReference dependency : dependencies) {
			steps.add(dependency);
			Collection<BetterDependency> dependencyDependencies = dependency.resolveList(this.dependencies);

			if (dependencyDependencies.stream().anyMatch(d -> d == start)) {
				throw new GrainInitializationException("Circular dependency detected:\n\n" + start.getType().getName() + "\n\t|\n\tv\n" + steps.stream().map(DependencyReference::getName).collect(Collectors.joining("\n\t|\n\tv\n")));
			}

			check(start, dependencyDependencies.stream().flatMap(d -> d.getDependencies().stream()).collect(Collectors.toList()), steps);
		}
	}
}
