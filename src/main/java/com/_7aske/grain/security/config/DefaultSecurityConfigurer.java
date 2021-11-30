package com._7aske.grain.security.config;

import com._7aske.grain.component.Default;
import com._7aske.grain.component.Grain;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.security.config.rule.Rule;

/**
 * Default SecurityConfigurer
 */
@Grain @Default final class DefaultSecurityConfigurer implements SecurityConfigurer {

	@Override
	public void configure(Rule.Builder builder) {
		builder
				.authenticated().urlPattern("/**").and()
				.unauthenticated().urlPattern("/login").method(HttpMethod.GET, HttpMethod.POST).and()
				.unauthenticated().urlPattern("/logout").method(HttpMethod.GET).and();
	}
}
