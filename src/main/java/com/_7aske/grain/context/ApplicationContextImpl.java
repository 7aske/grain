package com._7aske.grain.context;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;
import com._7aske.grain.util.classloader.GrainClassLoader;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class ApplicationContextImpl implements ApplicationContext {
	private final GrainRegistry grainRegistry;
	private final StaticLocationsRegistry staticLocationsRegistry;
	private final String basePackage;
	private final Configuration configuration;

	public ApplicationContextImpl(String basePackage, Configuration configuration, StaticLocationsRegistry staticLocationsRegistry) {
		GrainClassLoader classLoader = new GrainClassLoader(basePackage);
		this.basePackage = basePackage;
		this.staticLocationsRegistry = staticLocationsRegistry;
		this.configuration = configuration;
		this.grainRegistry = new GrainRegistry();
		this.grainRegistry.registerGrain(configuration);
		this.grainRegistry.registerGrains(classLoader.loadClasses(cl -> isAnnotationPresent(cl, Grain.class)));
	}

	public ApplicationContextImpl(String basePackage) {
		this(basePackage, Configuration.createDefault(), StaticLocationsRegistry.createDefault());
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
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public String getPackage() {
		return basePackage;
	}
}
