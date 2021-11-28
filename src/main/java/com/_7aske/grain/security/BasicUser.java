package com._7aske.grain.security;

import java.util.Collection;

public class BasicUser implements User {
	private final String username;
	private final String password;
	// @Note do we want to set roles after object creation?
	private final Collection<? extends Authority> authorities;

	public BasicUser(String username, String password, Collection<? extends Authority> authorities) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public Object getPassword() {
		return password;
	}

	@Override
	public Collection<? extends Authority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
