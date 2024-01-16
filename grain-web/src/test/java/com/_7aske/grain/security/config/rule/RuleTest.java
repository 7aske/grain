package com._7aske.grain.security.config.rule;

import com._7aske.grain.web.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RuleTest {

	@Test
	void testBuilder(){
		Rule.Builder builder = new Rule.Builder();
		List<Rule> rules = builder
				.urlPattern("/test/*/test")
				.unauthenticated()
				.roles("TEST_ROLE")
				.method(HttpMethod.GET)
				.build();

		assertEquals(1, rules.size());
		assertEquals("/test/*/test", rules.get(0).getPattern());
		assertEquals(1, rules.get(0).getRolesRequired().size());
		assertEquals(1, rules.get(0).getHttpMethods().size());
		assertEquals(1, rules.get(0).getHttpMethods().size());
		assertFalse(rules.get(0).isAuthenticationRequired());
	}
}