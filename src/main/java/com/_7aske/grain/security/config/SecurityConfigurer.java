package com._7aske.grain.security.config;

import com._7aske.grain.security.config.builder.SecurityConfigurationBuilder;

/**
 * Interface for configuring Security.
 */
public interface SecurityConfigurer {
	/**
	 * Called during SecurityConfigurer lifecycle. User to configure
	 * Security.
	 * @param builder for authentication rules.
	 */
	void configure(SecurityConfigurationBuilder builder);
}
