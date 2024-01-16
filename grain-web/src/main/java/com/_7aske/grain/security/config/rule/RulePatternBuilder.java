package com._7aske.grain.security.config.rule;

import com._7aske.grain.web.http.HttpMethod;

import java.util.List;

public interface RulePatternBuilder {
	RulePatternBuilder method(HttpMethod... methods);

	RulePatternBuilder authenticated();

	RulePatternBuilder unauthenticated();

	RulePatternBuilder roles(String... roles);

	RuleBuilder and();

	List<Rule> build();
}
