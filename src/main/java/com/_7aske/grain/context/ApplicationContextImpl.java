package com._7aske.grain.context;

import com._7aske.grain.GrainApp;
import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

public class ApplicationContextImpl implements ApplicationContext {
	private final GrainRegistry grainRegistry;
	private final StaticLocationsRegistry staticLocationsRegistry;
	private final String basePackage;
	private final Configuration configuration;
	private final Logger logger = LoggerFactory.getLogger(ApplicationContextImpl.class);

	public ApplicationContextImpl(String basePackage, Configuration configuration, StaticLocationsRegistry staticLocationsRegistry) {
		this.basePackage = basePackage;
		this.staticLocationsRegistry = staticLocationsRegistry;
		this.configuration = configuration;
		this.grainRegistry = new GrainRegistry();
		this.grainRegistry.registerGrain(configuration);
		// @Note We load user defined classes last
		this.grainRegistry.registerGrains(GrainApp.class.getPackageName());
		this.grainRegistry.registerGrains(basePackage);
		logger.info("Loaded {} Grain classes", grainRegistry.getGrains().size());
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

	@Override
	public <T> T getGrain(Class<T> clazz) {
		return grainRegistry.getGrain(clazz);
	}
}
