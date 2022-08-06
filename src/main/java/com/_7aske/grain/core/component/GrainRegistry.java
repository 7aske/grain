package com._7aske.grain.core.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.middleware.Middleware;
import com._7aske.grain.util.ReflectionUtil;
import com._7aske.grain.util.classloader.GrainJarClassLoader;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

/**
 * Class responsible for loading all classes that are the part of the
 * dependency injection system.
 */
public class GrainRegistry {
	private final DependencyContainer grains;
	private final BetterGrainInitializer grainInitializer;
	private final Logger logger = LoggerFactory.getLogger(GrainRegistry.class);
	private final Interpreter interpreter;

	public GrainRegistry(Configuration configuration) {
		this.grainInitializer = new BetterGrainInitializer(configuration);
		this.grains = this.grainInitializer.getContainer();

		this.interpreter = new Interpreter();
		interpreter.putProperties(configuration.getProperties());

		registerGrain(this);
	}

	public void registerGrains(String basePkg) {
		Set<Class<?>> classes = Arrays.stream(new String[]{GrainApp.class.getPackageName(), basePkg})
				.flatMap(pkg -> new GrainJarClassLoader(pkg).loadClasses(this::shouldBeLoaded).stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		grainInitializer.inject(classes);
	}

	private boolean shouldBeLoaded(Class<?> cl) {
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

	public Collection<Object> getControllers() {
		return grains.getAll()
				.stream()
				// @Note this should be implemented everywhere where grains
				// are fetched as a list
				.filter(d -> d.getType().isAnnotationPresent(Controller.class))
				.map(BetterDependency::getInstance)
				.sorted(ReflectionUtil::compareLibraryAndUserPackage)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Collection<Object> getGrains() {
		return grains.getAll()
				.stream()
				.map(BetterDependency::getInstance)
				.collect(Collectors.toList());
	}

	public <T> T getGrain(Class<T> clazz) {
		return grains.getByClass(clazz).orElseThrow(() -> new GrainRuntimeException("Grain not found: " + clazz))
				.getInstance();
	}

	public Collection<Middleware> getMiddlewares() {
		return grains.getListByClass(Middleware.class).stream()
				.sorted(ReflectionUtil::compareLibraryAndUserPackage)
				.map(BetterDependency::getInstance)
				.map(Middleware.class::cast)
				.collect(Collectors.toList());
	}

	public void registerGrain(Object object) {
		if (!isAnnotationPresent(object.getClass(), Grain.class)) {
			logger.warn("Registered Grain {} without @Grain annotation", object.getClass());
		}
		BetterDependency betterDependency = new BetterDependency(
				object.getClass(),
				GrainNameResolver.getDefault().resolveReferenceName(object.getClass()));
		betterDependency.setInstance(object);
		grains.add(betterDependency);
	}
}