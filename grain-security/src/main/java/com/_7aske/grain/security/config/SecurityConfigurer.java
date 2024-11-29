package com._7aske.grain.security.config;

import com._7aske.grain.security.config.builder.SecurityConfigurationBuilder;

/**
 * Interface for configuring Security. This should be implemented by the user
 * configurer Grain instance.
 */
public interface SecurityConfigurer {
	/**
	 * Called during SecurityConfigurer lifecycle. User to configure
	 * Security.
	 * @param builder for authentication rules.
	 */
	void configure(SecurityConfigurationBuilder builder);
}
