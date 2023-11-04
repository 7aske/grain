package com._7aske.grain.core.configuration;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

// @Warning we manually add this class in the dependency injection pipeline
// do not mark it with @Grain
public final class Configuration extends AbstractConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	public static final String PROFILES_ENV_VARIABLE = "GRAIN_PROFILES_ACTIVE";

	private Configuration() {
		super(new Properties());

		String profilesString = Optional.ofNullable(System.getenv(PROFILES_ENV_VARIABLE))
				.orElse(",");
		List<String> profiles = Arrays.stream(profilesString
						.split("\\s*,\\s*"))
				.toList();


		PropertiesResolver propertiesResolver = new PropertiesResolver(profiles);
		propertiesResolver.resolve("META-INF/application",
				properties::load);
		propertiesResolver.resolve("application",
				properties::load);

		EnvironmentResolver environmentResolver = new EnvironmentResolver();
		environmentResolver.resolve(this::set);

		properties.forEach((key, value) ->
				System.setProperty(key.toString(), value.toString()));

		logger.info("Active profiles: {}", profiles);
	}

	public static Configuration createDefault() {
		return new Configuration();
	}
}
