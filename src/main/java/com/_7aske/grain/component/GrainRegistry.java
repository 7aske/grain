package com._7aske.grain.component;

import com._7aske.grain.requesthandler.middleware.Middleware;

import java.util.*;
import java.util.stream.Collectors;

public class GrainRegistry {
	private final Map<Class<?>, Object> grains = new HashMap<>();
	private final String basePackage;
	private final String userBasePackage;

	public GrainRegistry(String basePackage, String userBasePackage) {
		if (basePackage == null)
			throw new IllegalArgumentException("Base package must not be null");
		this.basePackage = basePackage;
		this.userBasePackage = userBasePackage;
		doInitializeGrains();
	}

	private void doInitializeGrains() {
		Set<Class<?>> grainClasses = new GrainLoader(basePackage).loadGrains();
		grains.putAll(new GrainInitializer(grainClasses).getLoaded());

		Set<Class<?>> userGrainClasses = new GrainLoader(userBasePackage).loadGrains();
		grains.putAll(new GrainInitializer(userGrainClasses).getLoaded());
	}

	public Set<Object> getControllers() {
		return grains.values().stream()
				.filter(g -> g.getClass().isAnnotationPresent(Controller.class))
				.collect(Collectors.toSet());
	}

	public Set<Object> getGrains() {
		return new HashSet<>(grains.values());
	}

	public <T> T getGrain(Class<T> clazz) {
		return clazz.cast(grains.get(clazz));
	}

	public Set<Middleware> getMiddlewares() {
		return grains.values().stream()
				.filter(g -> Arrays.asList(g.getClass().getInterfaces()).contains(Middleware.class))
				.map(Middleware.class::cast)
				.collect(Collectors.toSet());
	}
}