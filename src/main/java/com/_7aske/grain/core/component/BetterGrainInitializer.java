package com._7aske.grain.core.component;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BetterGrainInitializer {
	private final DependencyContainer dependencies;
	private final Interpreter interpreter;
	private final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();

	public BetterGrainInitializer(Configuration configuration) {
		this.dependencies = new DependencyContainer();
		this.interpreter = new Interpreter();
		interpreter.putProperties(configuration.getProperties());
	}


	// @Todo rename
	public DependencyContainer initialize(Set<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			BetterDependency dependency = new BetterDependency(
					clazz,
					grainNameResolver.resolveReferenceName(clazz)
			);
			dependency.getGrainMethods().forEach(method -> {
				BetterDependency methodDependency = new BetterDependency(
						method.getReturnType(),
						grainNameResolver.resolveReferenceName(method));
				methodDependency.setGrainMethodDependency(true);
				methodDependency.setProvider(dependency);
				this.dependencies.add(methodDependency);
			});
			this.dependencies.add(dependency);
		}

		checkCircularDependencies();

		for (BetterDependency dependency : this.dependencies) {
			if (!dependency.isGrainMethodDependency()) {
				initialize(dependency);
			}
		}

		return dependencies;
	}

	private void initialize(BetterDependency dependency) {
		if (dependency.isInitialized()) {
			return;
		}
		Constructor<?> constructor = dependency.getConstructor();
		Object[] constructorParameters = mapConstructorParametersToDependencies(dependency);
		try {
			Object instance = ReflectionUtil.newInstance(constructor, constructorParameters)
					.orElseThrow(() -> new GrainInitializationException("Could not instantiate " + dependency.getType()));
			dependency.setInstance(instance);
			dependency.setInitialized(true);
		} catch (Exception e) {
			throw new GrainInitializationException( "Failed to initialize grain " + dependency, e);
		}

		dependency.getGrainMethods().forEach(method -> {
			try {
				Object instance = dependency.getInstance();
				Object result = ReflectionUtil.invokeMethod(method, instance, mapMethodParametersToDependencies(method));
				BetterDependency methodDependency = DependencyReference.of(method)
						.resolve(dependencies);
				if (methodDependency.isInitialized()) {
					throw new GrainInitializationException("Dependency '" + methodDependency + "' is already initialized");
				} else {
					methodDependency.setInstance(result);
					methodDependency.setInitialized(true);
				}
			} catch (Exception e) {
				throw new GrainInitializationException( "Failed to initialize grain " + dependency, e);
			}
		});

	}

	private Object[] mapMethodParametersToDependencies(Method method) {
		Parameter[] parameters = method.getParameters();
		Object[] methodParameters = new Object[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			BetterDependency dependency = DependencyReference.of(parameters[i])
					.resolve(dependencies);
			if (!dependency.isInitialized()) {
				if (dependency.isGrainMethodDependency()
						&& !dependency.getProvider().isInitialized()) {
					initialize(dependency.getProvider());
				}
				initialize(dependency);
			}
			methodParameters[i] = dependency.getInstance();
		}
		return methodParameters;
	}

	private Object[] mapConstructorParametersToDependencies(BetterDependency betterDependency) {
		DependencyReference[] constructorParameters = betterDependency.getConstructorParameters();
		Object[] parameterInstances = new Object[constructorParameters.length];
		for (int i = 0; i < constructorParameters.length; i++) {
			DependencyReference dependencyReference = constructorParameters[i];
			BetterDependency dependency = dependencyReference.resolve(this.dependencies);
			if (!dependency.isInitialized())
				initialize(dependency);
			parameterInstances[i] = dependency.getInstance();
		}
		return parameterInstances;
	}

	public void checkCircularDependencies() {
		for (BetterDependency dependency : dependencies.getAll()) {
			check(dependency, dependency.getDependencies(), new ArrayList<>());
		}
	}

	private void check(BetterDependency start, List<DependencyReference> dependencies, List<DependencyReference> steps) {
		for (DependencyReference dependency : dependencies) {
			steps.add(dependency);
			BetterDependency dependencyDependencies = dependency.resolve(this.dependencies);

			if (dependencyDependencies == start) {
				throw new GrainInitializationException("Circular dependency detected:\n\n" + start.asReference().getName() + "\n\t|\n\tv\n" + steps.stream().map(DependencyReference::getName).collect(Collectors.joining("\n\t|\n\tv\n")));
			}

			check(start, dependencyDependencies.getDependencies(), steps);
		}
	}
}
