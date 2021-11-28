package com._7aske.grain.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.exception.GrainMultipleImplementationsException;
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
		Set<Class<?>> classes = Arrays.stream(new String[]{GrainApp.class.getPackageName(), basePkg})
				.flatMap(pkg -> new GrainJarClassLoader(pkg).loadClasses(cl -> !cl.isAnnotation() && isAnnotationPresent(cl, Grain.class)).stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		grains.putAll(grainInitializer.initialize(classes));
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
		List<T> result = grains.values().stream()
				.filter(g -> clazz.isAssignableFrom(g.getClass()))
				.map(clazz::cast)
				.collect(Collectors.toList());

		// @CopyPasta GrainInitializer
		if (result.size() > 1) {
			// User defined dependencies are the ones that do not start
			// with grain library base package which is the package of
			// GrainApp.class.
			List<T> userDefined = result.stream()
					.filter(g -> {
						String basePackage = GrainApp.class.getPackageName();
						String depPackage = g.getClass().getPackageName();

						// If the package is not starting with package but if it is make sure by checking whether the next
						// letter after the basePackage is a dot since in case of com._7aske.grain as basePackge and
						// com._7aske.graintest only by checking starts with would return true. This can be refactored
						// to match paths like we do it for url path matching.
						return !(depPackage.startsWith(basePackage) &&
								depPackage.charAt(basePackage.length()) == '.');
					})
					.collect(Collectors.toList());
			if (userDefined.size() > 1) {
				if (userDefined.stream().noneMatch(g -> isAnnotationPresent(g.getClass(), Primary.class))) {
					throw new GrainMultipleImplementationsException(clazz);
				} else {
					// @Incomplete Handle the case where use has defined
					// multiple @Primary grains
					return userDefined.stream()
							.filter(g -> isAnnotationPresent(g.getClass(), Primary.class))
							.findFirst().orElse(null);
				}
			} else if (userDefined.size() == 1) {
				return userDefined.get(0);
			}
		}

		if (result.isEmpty()) {
			return null;
		}

		// in any other case just return the first found dependency
		return result.get(0);
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