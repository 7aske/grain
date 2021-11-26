package com._7aske.grain.component;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.middleware.Middleware;
import com._7aske.grain.util.classloader.GrainJarClassLoader;

import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class GrainRegistry {
	private final Map<Class<?>, Object> grains = new HashMap<>();
	private final GrainInitializer grainInitializer;
	private final Logger logger = LoggerFactory.getLogger(GrainRegistry.class);

	public GrainRegistry() {
		this.grainInitializer = new GrainInitializer();
		registerGrain(this);
	}

	public void registerGrains(String basePkg) {
		grains.putAll(grainInitializer.initialize(new GrainJarClassLoader(basePkg)
				.loadClasses(cl -> !cl.isAnnotation() && isAnnotationPresent(cl, Grain.class))));
	}

	public void registerGrains(Set<Class<?>> grainClasses) {
		grains.putAll(grainInitializer.initialize(grainClasses));
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

	public void registerGrain(Class<?> clazz) {
		if (!isAnnotationPresent(clazz, Grain.class)) {
			throw new GrainRuntimeException(String.format("%s must be annotated with @Grain", clazz));
		}
		if (grains.containsKey(clazz)) {
			throw new GrainRuntimeException(String.format("%s is already registered as a Grain", clazz));
		}
		grains.put(clazz, grainInitializer.initialize(clazz));
	}

	public void registerGrain(Object object) {
		if (!isAnnotationPresent(object.getClass(), Grain.class)) {
			logger.warn("Registered Grain {} without @Grain annotation", object.getClass());
		}
		if (grains.containsKey(object.getClass())) {
			throw new GrainRuntimeException(String.format("%s is already registered as a Grain", object.getClass()));
		}
		grains.replace(object.getClass(), grainInitializer.addInitialized(object));
	}
}