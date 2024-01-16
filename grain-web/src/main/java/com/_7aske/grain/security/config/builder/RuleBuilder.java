package com._7aske.grain.security.config.builder;

public interface RuleBuilder {

	RulePatternBuilder urlPattern(String pattern);

	SecurityConfigurationBuilder buildRules();
}
