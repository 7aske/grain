package com._7aske.grain.security.config;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.config.builder.SecurityConfigurationBuilder;

import java.util.UUID;

/**
 * Default SecurityConfigurer
 */
@Grain final class DefaultSecurityConfigurer implements SecurityConfigurer {
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

		// @Note important to print, otherwise the user wouldn't be able to know
		// the password and login.
		logger.info("Created default user with username: {} and password: {}", username, password);
	}

}
