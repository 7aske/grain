package com._7aske.grain.security.config;

import com._7aske.grain.component.Default;
import com._7aske.grain.component.Grain;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.config.builder.SecurityConfigurationBuilder;

import java.util.UUID;

/**
 * Default SecurityConfigurer
 */
@Grain @Default final class DefaultSecurityConfigurer implements SecurityConfigurer {
	private final Logger logger = LoggerFactory.getLogger(DefaultSecurityConfigurer.class);

	@Override
	public void configure(SecurityConfigurationBuilder builder) {
		String username = "root";
		String password = UUID.randomUUID().toString();
		// @formatter:off;
		builder.withRules()
					.urlPattern("/**").authenticated().and()
					.urlPattern("/login").unauthenticated().method(HttpMethod.GET, HttpMethod.POST).and()
					.urlPattern("/logout").unauthenticated().method(HttpMethod.GET).and()
				.buildRules()
				.withDefaultUser()
					.username(username)
					.password(password)
				.buildDefaultUser();
		// @formatter:on

		logger.info("Created default user with username: {} and password: {}", username, password);
	}

}
