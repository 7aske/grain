package com._7aske.grain.security.config.builder;

public interface SecurityConfigurationBuilder {
	RuleBuilder withRules();
	DefaultUserBuilder withDefaultUser();
	SecurityConfigurationBuilder authenticationSuccessUrl(String url);
	SecurityConfigurationBuilder authenticationFailureUrl(String url);
}
