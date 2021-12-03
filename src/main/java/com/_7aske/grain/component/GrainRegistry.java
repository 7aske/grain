package com._7aske.grain.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.middleware.Middleware;
import com._7aske.grain.util.ReflectionUtil;
import com._7aske.grain.util.classloader.GrainJarClassLoader;

import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.findClassByClass;
import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

/**
 * Class responsible for loading all classes that are the part of the
 * dependency injection system.
 */
public class GrainRegistry {
	private final Map<Class<?>, Object> grains = new HashMap<>();
	private final GrainInitializer grainInitializer;
	private final Logger logger = LoggerFactory.getLogger(GrainRegistry.class);
	private final Interpreter interpreter;

	public GrainRegistry(Configuration configuration) {
		this.grainInitializer = new GrainInitializer(configuration);

		this.interpreter = new Interpreter();
		interpreter.putProperties(configuration.getProperties());

		registerGrain(this);
	}

	public void registerGrains(String basePkg) {
		Set<Class<?>> classes = Arrays.stream(new String[]{GrainApp.class.getPackageName(), basePkg})
				.flatMap(pkg -> new GrainJarClassLoader(pkg).loadClasses(this::shouldBeLoaded).stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		grains.putAll(grainInitializer.initialize(classes));
	}

	public boolean shouldBeLoaded(Class<?> cl) {
		boolean isMarked = !cl.isAnnotation() && isAnnotationPresent(cl, Grain.class);
		if (cl.isAnnotationPresent(Condition.class)) {
			Condition condition = cl.getAnnotation(Condition.class);
			if (condition.value() == null || condition.value().isBlank()) {
				return isMarked;
			} else {
				String code = condition.value();
				boolean result = Boolean.parseBoolean(String.valueOf(interpreter.evaluate(code)));
				return isMarked && result;
			}
		} else {
			return isMarked;
		}
	}

	public Set<Object> getControllers() {
		return grains.values().stream()
				// @Note this should be implemented everywhere where grains
				// are fetched as a list
				.sorted(ReflectionUtil::compareLibraryAndUserPackage)
				.filter(g -> g.getClass().isAnnotationPresent(Controller.class))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<Object> getGrains() {
		return new HashSet<>(grains.values());
	}

	public <T> T getGrain(Class<T> clazz) {
		return clazz.cast(findClassByClass(clazz, grains.values(), Object::getClass).orElse(null));
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