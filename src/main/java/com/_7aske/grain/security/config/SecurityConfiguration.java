package com._7aske.grain.security.config;

import com._7aske.grain.component.Grain;
import com._7aske.grain.security.config.rule.Rule;

import java.util.List;

// @Refactor
/**
 * Default security configuration
 */
@Grain
public final class SecurityConfiguration {
	private List<Rule> rules;

	public List<Rule> getRules() {
		return rules;
	}

	void setRules(List<Rule> rules) {
		this.rules = rules;
	}
}
