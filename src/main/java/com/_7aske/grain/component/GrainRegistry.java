package com._7aske.grain.component;

import com._7aske.grain.requesthandler.middleware.Middleware;

import java.util.*;
import java.util.stream.Collectors;

public class GrainRegistry {
	private Map<Class<?>, Object> grains = new HashMap<>();
	private final String basePackage;

	public GrainRegistry(String pkg) {
		if (pkg == null)
			throw new IllegalArgumentException("Base package must not be null");
		this.basePackage = pkg;
		doInitializeGrains();
	}

	private void doInitializeGrains() {
		Set<Class<?>> grainClasses = new GrainLoader(basePackage).loadGrains();
		grains = new GrainInitializer(grainClasses).getLoaded();
	}

	public Set<Object> getControllers() {
		return grains.values().stream()
				.filter(g -> g.getClass().isAnnotationPresent(Controller.class))
				.collect(Collectors.toSet());
	}

	public Set<Object> getGrains() {
		return new HashSet<>(grains.values());
	}

	public Set<Middleware> getMiddlewares() {
		return grains.values().stream()
				.filter(g -> Arrays.asList(g.getClass().getInterfaces()).contains(Middleware.class))
				.map(Middleware.class::cast)
				.collect(Collectors.toSet());
	}
}