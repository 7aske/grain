package com._7aske.grain.context;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.orm.connection.ConnectionManager;
import com._7aske.grain.orm.database.DatabaseExecutor;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

public class ApplicationContextImpl implements ApplicationContext {
	private final GrainRegistry grainRegistry;
	private final StaticLocationsRegistry staticLocationsRegistry;
	private final String basePackage;
	private final Configuration configuration;
	private final com._7aske.grain.logging.Logger Logger = LoggerFactory.getLogger(ApplicationContextImpl.class);

	public ApplicationContextImpl(String basePackage, Configuration configuration, StaticLocationsRegistry staticLocationsRegistry) {
		this.basePackage = basePackage;
		this.staticLocationsRegistry = staticLocationsRegistry;
		this.configuration = configuration;
		this.grainRegistry = new GrainRegistry();
		this.grainRegistry.registerGrain(configuration);
		// @Temporary proper loading of classes inside the Grain framework should replace
		// this hack of ad-hoc registering of grains. Grains originating from the framework
		// itself should be loaded with the same mechanism as user-created classes.
		this.grainRegistry.registerGrain(ConnectionManager.class);
		this.grainRegistry.registerGrain(DatabaseExecutor.class);
		// @Note We load user defined classes last
		this.grainRegistry.registerGrains(basePackage);
		Logger.info("Loaded {} Grain classes", grainRegistry.getGrains().size());
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
