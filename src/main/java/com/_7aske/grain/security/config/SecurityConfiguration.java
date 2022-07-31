package com._7aske.grain.security.config;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.security.BasicUser;
import com._7aske.grain.security.config.rule.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Default security configuration
 */
@Grain
public final class SecurityConfiguration {
	private List<Rule> rules = new ArrayList<>();
	private BasicUser defaultUser;
	private String authenticationSuccessUrl = "/";
	private String authenticationFailureUrl = "/login";

	public String getAuthenticationSuccessUrl() {
		return authenticationSuccessUrl;
	}

	public void setAuthenticationSuccessUrl(String authenticationSuccessUrl) {
		this.authenticationSuccessUrl = authenticationSuccessUrl;
	}

	public String getAuthenticationFailureUrl() {
		return authenticationFailureUrl;
	}

	public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
		this.authenticationFailureUrl = authenticationFailureUrl;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setDefaultUser(BasicUser defaultUser) {
		if (defaultUser == null)
			throw new NullPointerException();
		this.defaultUser = defaultUser;
	}

	public BasicUser getDefaultUser() {
		return defaultUser;
	}

	public void setRules(List<Rule> rules) {
		if (rules == null)
			throw new NullPointerException();
		this.rules = rules;
	}
}
