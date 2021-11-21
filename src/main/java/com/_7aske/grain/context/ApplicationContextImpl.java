package com._7aske.grain.context;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;
import com._7aske.grain.util.classloader.GrainClassLoader;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class ApplicationContextImpl implements ApplicationContext {
	private final GrainRegistry grainRegistry;
	private final StaticLocationsRegistry staticLocationsRegistry;
	private final String basePackage;

	public ApplicationContextImpl(String basePackage) {
		this.basePackage = basePackage;
		GrainClassLoader classLoader = new GrainClassLoader(basePackage);
		grainRegistry = new GrainRegistry();
		staticLocationsRegistry = new StaticLocationsRegistry();

		grainRegistry.registerGrains(classLoader.loadClasses(cl -> isAnnotationPresent(cl, Grain.class)));
	}

	@Override
	public GrainRegistry getGrainRegistry() {
		return grainRegistry;
	}

	@Override
	public StaticLocationsRegistry getStaticLocationsRegistry() {
		return staticLocationsRegistry;
	}

	@Override
	public String getPackage() {
		return basePackage;
	}
}
