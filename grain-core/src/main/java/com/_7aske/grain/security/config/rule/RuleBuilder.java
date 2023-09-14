package com._7aske.grain.security.config.rule;

import java.util.List;

public interface RuleBuilder {
	RulePatternBuilder urlPattern(String pattern);

	List<Rule> build();
}
