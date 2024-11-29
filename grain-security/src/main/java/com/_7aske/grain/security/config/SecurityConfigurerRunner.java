package com._7aske.grain.security.config;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.security.config.builder.SecurityConfigurationBuilder;

/**
 * Class responsible for initializing user or default {@link SecurityConfiguration}.
 */
@Grain
final class SecurityConfigurerRunner {

	SecurityConfigurerRunner(SecurityConfigurer securityConfigurer, SecurityConfigurationBuilder builder, Configuration configuration) {
		if (Boolean.TRUE.equals(configuration.getBoolean(ConfigurationKey.SECURITY_ENABLED))) {
			securityConfigurer.configure(builder);
		}
	}
}
