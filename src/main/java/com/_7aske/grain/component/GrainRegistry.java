package com._7aske.grain.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.requesthandler.middleware.Middleware;
import com._7aske.grain.util.classloader.GrainClassLoader;

import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class GrainRegistry {
	private final Map<Class<?>, Object> grains = new HashMap<>();
	private final String[] packages;

	public GrainRegistry(String... packages) {
		if (packages.length == 0)
			packages = new String[]{GrainApp.class.getPackageName()};
		this.packages = packages;
		doInitializeGrains();
	}

	private void doInitializeGrains() {
		for (String basePackage : packages) {
			Set<Class<?>> grainClasses = new GrainClassLoader(basePackage)
					.loadClasses(c -> isAnnotationPresent(c, Grain.class));
			grains.putAll(new GrainInitializer(grainClasses).getLoaded());
		}

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

	public void registerGrain(Object object) {
		if (!isAnnotationPresent(object.getClass(), Grain.class)) {
			throw new GrainRuntimeException(String.format("%s must be annotated with @Grain", object.getClass()));
		}
		if (grains.containsKey(object.getClass())) {
			throw new GrainRuntimeException(String.format("%s is already registered as a Grain", object.getClass()));
		}
		grains.put(object.getClass(), object);
	}
}