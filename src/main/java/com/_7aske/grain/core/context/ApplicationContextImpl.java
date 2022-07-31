package com._7aske.grain.core.context;

import com._7aske.grain.core.component.GrainRegistry;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

public class ApplicationContextImpl implements ApplicationContext {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextImpl.class);
	private final GrainRegistry grainRegistry;
	private final String basePackage;
	private final Configuration configuration;

	public ApplicationContextImpl(String basePackage, Configuration configuration) {
		this.basePackage = basePackage;
		this.configuration = configuration;
		this.grainRegistry = new GrainRegistry(configuration);
		this.grainRegistry.registerGrain(configuration);
		this.grainRegistry.registerGrains(basePackage);
		logger.info("Loaded {} Grain classes", grainRegistry.getGrains().size());
	}

	public ApplicationContextImpl(String basePackage) {
		this(basePackage, Configuration.createDefault());
	}

	@Override
	public GrainRegistry getGrainRegistry() {
		return grainRegistry;
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
