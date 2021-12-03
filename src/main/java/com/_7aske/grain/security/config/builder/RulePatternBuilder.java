package com._7aske.grain.security.config.builder;

import com._7aske.grain.http.HttpMethod;

public interface RulePatternBuilder {

	RulePatternBuilder method(HttpMethod... methods);

	RulePatternBuilder authenticated();

	RulePatternBuilder unauthenticated();

	RulePatternBuilder roles(String... roles);

	RuleBuilder and();

	SecurityConfigurationBuilder buildRules();
}
