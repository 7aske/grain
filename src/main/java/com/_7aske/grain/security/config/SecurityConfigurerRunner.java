package com._7aske.grain.security.config;

import com._7aske.grain.component.AfterInit;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.config.ConfigurationKey;
import com._7aske.grain.security.config.builder.SecurityConfigurationBuilder;

import java.util.Objects;

/**
 * Class responsible for initializing user or default {@link SecurityConfiguration}.
 */
@Grain
final class SecurityConfigurerRunner {
	@Inject
	private SecurityConfigurer securityConfigurer;
	@Inject
	private SecurityConfigurationBuilder builder;
	@Inject
	private Configuration configuration;

	@AfterInit
	private void setup() {
		if (Objects.equals(configuration.getProperty(ConfigurationKey.SECURITY_ENABLED), true)) {
			securityConfigurer.configure(builder);
		} else {

		}
	}
}
