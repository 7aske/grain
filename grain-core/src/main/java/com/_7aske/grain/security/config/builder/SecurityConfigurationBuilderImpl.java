package com._7aske.grain.security.config.builder;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.security.Authority;
import com._7aske.grain.security.BasicAuthority;
import com._7aske.grain.security.BasicUser;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.config.rule.Rule;
import com._7aske.grain.security.crypto.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// @Refactor This is probably a hugely unnecessary attempt to be elegant.
@Grain
final class SecurityConfigurationBuilderImpl implements SecurityConfigurationBuilder, RuleBuilder, RulePatternBuilder, DefaultUserBuilder {
	@Inject
	private PasswordEncoder passwordEncoder;
	private final SecurityConfiguration configuration;
	private Rule.Builder ruleBuilder;
	private BasicUser defaultUser;

	public SecurityConfigurationBuilderImpl(SecurityConfiguration configuration) {
		this.configuration = configuration;
		this.ruleBuilder = null;
		this.defaultUser = null;
	}

	@Override
	public RuleBuilder withRules() {
		this.ruleBuilder = new Rule.Builder();
		return this;
	}

	@Override
	public DefaultUserBuilder withDefaultUser() {
		this.defaultUser = new BasicUser();
		return this;
	}

	@Override
	public SecurityConfigurationBuilder authenticationSuccessUrl(String url) {
		this.configuration.setAuthenticationSuccessUrl(url);
		return this;
	}

	@Override
	public SecurityConfigurationBuilder authenticationFailureUrl(String url) {
		this.configuration.setAuthenticationFailureUrl(url);
		return this;
	}

	@Override
	public DefaultUserBuilder username(String username) {
		this.defaultUser.setUsername(username);
		return this;
	}

	@Override
	public DefaultUserBuilder password(String password) {
		this.defaultUser.setPassword(passwordEncoder.encode(password));
		return this;
	}

	@Override
	public DefaultUserBuilder authorities(Collection<? extends Authority> roles) {
		this.defaultUser.getAuthorities().addAll(roles);
		return this;
	}

	@Override
	public DefaultUserBuilder authorities(String... roles) {
		this.defaultUser.getAuthorities().addAll(Arrays.stream(roles).map(BasicAuthority::new).collect(Collectors.toList()));
		return this;
	}

	@Override
	public SecurityConfigurationBuilder buildDefaultUser() {
		this.configuration.setDefaultUser(this.defaultUser);
		return this;
	}

	@Override
	public RulePatternBuilder urlPattern(String pattern) {
		this.ruleBuilder.urlPattern(pattern);
		return this;

	}

	@Override
	public RulePatternBuilder method(HttpMethod... methods) {
		this.ruleBuilder.method(methods);
		return this;
	}

	@Override
	public RulePatternBuilder authenticated() {
		this.ruleBuilder.authenticated();
		return this;
	}

	@Override
	public RulePatternBuilder unauthenticated() {
		this.ruleBuilder.unauthenticated();
		return this;
	}

	@Override
	public RulePatternBuilder roles(String... roles) {
		this.ruleBuilder.roles(roles);
		return this;
	}

	@Override
	public RuleBuilder and() {
		this.ruleBuilder.and();
		return this;
	}

	@Override
	public SecurityConfigurationBuilder buildRules() {
		List<Rule> rules = this.ruleBuilder.build();
		this.configuration.setRules(rules);
		return this;
	}

	public SecurityConfiguration build() {
		return this.configuration;
	}
}
