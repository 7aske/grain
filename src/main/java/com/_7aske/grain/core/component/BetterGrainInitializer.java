package com._7aske.grain.core.component;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainDependencyUnsatisfiedException;
import com._7aske.grain.exception.GrainInitializationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class BetterGrainInitializer {
	private final Map<String, BetterDependency> dependencies;
	private final Interpreter interpreter;
	private final GrainNameResolver grainNameResolver = GrainNameResolver.getDefault();

	public BetterGrainInitializer(Configuration configuration) {
		this.dependencies = new ConcurrentHashMap<>();
		this.interpreter = new Interpreter();
		interpreter.putProperties(configuration.getProperties());
	}


	public Map<String, Object> initialize(Set<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			String name = grainNameResolver.resolveDeclarationName(clazz);
			BetterDependency dependency = dependencies.getOrDefault(name, new BetterDependency(name, clazz));
			resolveDependencies(dependency);
			dependencies.putIfAbsent(name, dependency);
		}
		checkCircularDependencies();
		return null;
	}

	private void resolveDependencies(BetterDependency betterDependency) {
		// Firstly, we resolve constructor parameters
		List<String> constructionDependencies = Arrays.stream(betterDependency.getParams())
				.map(grainNameResolver::resolveReferenceName)
				.collect(Collectors.toList());
		betterDependency.setDependencies(constructionDependencies);

		// Secondly, we resolve any possible @Grain methods
		List<String> grainDependencies = Arrays.stream(betterDependency.getClazz().getDeclaredMethods())
				.filter(m -> isAnnotationPresent(m, Grain.class))
				.flatMap(m -> {
					String name = grainNameResolver.resolveDeclarationName(m);
					// This is in turn a dependency declaration
					if (dependencies.containsKey(name)) {
						throw new GrainInitializationException("Grain method " + m.getName() + " returns a dependency " + name + " which is already defined");
					}
					BetterDependency dependency = new BetterDependency(name, m.getReturnType());
					dependency.setProvider(betterDependency.getName());
					resolveDependencies(dependency);

					// And their dependencies
					return Stream.concat(Stream.of(name), Arrays.stream(m.getParameters())
							.map(grainNameResolver::resolveReferenceName));
				})
				.collect(Collectors.toList());
		betterDependency.getDependencies().addAll(grainDependencies);
	}

	public void checkCircularDependencies() {
		for (BetterDependency dependency : dependencies.values()) {
			check(dependency, dependency.getDependencies(), new ArrayList<>());
		}
	}

	private void check(BetterDependency start, List<String> dependencies, List<String> steps) {
		for (String dependency : dependencies) {
			steps.add(dependency);
			BetterDependency betterDependency = this.dependencies.get(dependency);
			if (betterDependency == start) {
				throw new GrainInitializationException("Circular dependency detected:\n\n" + start.getName() + "\n\t|\n\tv\n" + String.join("\n\t|\n\tv\n", steps));
			}

			if (betterDependency == null) {
				throw new GrainDependencyUnsatisfiedException("Dependency '" + dependency + "' is not satisfied for '" + start.getName() + "' dependency chain.");
			}

			check(start, betterDependency.getDependencies(), steps);
		}
	}
}
