package com._7aske.grain.security.config.builder;

import com._7aske.grain.security.Authority;

import java.util.Collection;

public interface DefaultUserBuilder {
	DefaultUserBuilder username(String username);
	DefaultUserBuilder password(String password);
	DefaultUserBuilder authorities(String... roles);
	DefaultUserBuilder authorities(Collection<? extends Authority> roles);
	SecurityConfigurationBuilder buildDefaultUser();
}
