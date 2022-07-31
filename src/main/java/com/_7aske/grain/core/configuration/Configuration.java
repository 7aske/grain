package com._7aske.grain.core.configuration;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.logging.LogManager.getLogManager;

// @Warning we manually add this class in the dependency injection pipeline
// do not mark it with @Grain
public final class Configuration extends AbstractConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	public static final String PROFILES_ENV_VARIABLE = "GRAIN_PROFILES_ACTIVE";

	private Configuration() {
		super(new Properties());
		set(ConfigurationKey.SERVER_HOST, "0.0.0.0");
		set(ConfigurationKey.SERVER_PORT, 8080);
		set(ConfigurationKey.SERVER_THREADS, 100);
		set(ConfigurationKey.REQUEST_HANDLER_ACCESS_LOG, true);
		set(ConfigurationKey.SESSION_ENABLED, true);
		set(ConfigurationKey.SECURITY_ENABLED, false);

		String profilesString = Optional.ofNullable(System.getenv(PROFILES_ENV_VARIABLE))
				.orElse(",");
		List<String> profiles = Arrays.stream(profilesString
						.split("\\s*,\\s*"))
				.collect(Collectors.toList());

		logger.info("Active profiles: {}", profiles);

		PropertiesResolver propertiesResolver = new PropertiesResolver(profiles);
		propertiesResolver.resolve("application",
				properties::load);
		propertiesResolver.resolve("logging", is ->
				getLogManager().readConfiguration(is));

		EnvironmentResolver environmentResolver = new EnvironmentResolver();
		environmentResolver.resolve(this::set);

		properties.forEach((key, value) ->
				System.setProperty(key.toString(), value.toString()));
	}

	public static Configuration createDefault() {
		return new Configuration();
	}
}
