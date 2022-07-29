package com._7aske.grain.security.config.builder;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.crypto.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigurationBuilderTest {
	SecurityConfigurationBuilderImpl builder;
	PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		ApplicationContextHolder.setContext(null);
		ApplicationContext applicationContext = new ApplicationContextImpl(SecurityConfigurationBuilderTest.class.getPackageName());
		builder = applicationContext.getGrain(SecurityConfigurationBuilderImpl.class);
		passwordEncoder = applicationContext.getGrain(PasswordEncoder.class);
	}

	@Test
	void testSecurityConfigurationBuilder() {
		SecurityConfiguration configuration = ((SecurityConfigurationBuilderImpl)builder
				.withDefaultUser()
				.username("test")
				.password("test")
				.buildDefaultUser()
				.withRules()
				.urlPattern("/test").authenticated().method(HttpMethod.DELETE).roles("ROLE")
				.buildRules())
				.build();

		assertEquals("/test", configuration.getRules().get(0).getPattern());
		assertEquals(1, configuration.getRules().get(0).getRolesRequired().size());
		assertEquals("test", configuration.getDefaultUser().getUsername());
		assertTrue(passwordEncoder.matches("test", (String) configuration.getDefaultUser().getPassword()));
		assertEquals(1, configuration.getRules().get(0).getHttpMethods().size());
	}
}