package com._7aske.grain.security.config;

import com._7aske.grain.component.AfterInit;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.config.rule.Rule;

/**
 * Class responsible for initializing user or default {@link SecurityConfiguration}.
 */
@Grain
final class SecurityConfigurerRunner {
	@Inject
	private SecurityConfigurer securityConfigurer;
	@Inject
	private Configuration configuration;
	@Inject
	private SecurityConfiguration securityConfiguration;

	private final Logger logger = LoggerFactory.getLogger(DefaultSecurityConfigurer.class);

	@AfterInit
	private void setup() {
		Rule.Builder builder = new Rule.Builder();
		securityConfigurer.configure(builder);
		securityConfiguration.setRules(builder.build());
	}
}
