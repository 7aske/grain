package com._7aske.grain.security.config;

import com._7aske.grain.security.config.rule.Rule;

/**
 * Interface for configuring Security.
 */
public interface SecurityConfigurerAdapter {
	/**
	 * Called during SecurityConfigurer lifecycle. User to configure
	 * Security.
	 * @param builder for authentication rules.
	 */
	void configure(Rule.Builder builder);
}
