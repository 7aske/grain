package com._7aske.grain.security.config;

import com._7aske.grain.component.Grain;
import com._7aske.grain.security.config.rule.Rule;

import java.util.List;

// @Refactor
@Grain
public class SecurityConfiguration {
	private List<Rule> rules;
}
