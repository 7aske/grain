package com._7aske.grain.security;

import java.util.Collection;

public class BasicAuthentication implements Authentication {
	private final User user;
	private Collection<? super Authority> authorities;

	public BasicAuthentication(User user, Collection<? super Authority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}

	public BasicAuthentication(User user) {
		this.user = user;
		this.authorities = user.getAuthorities();
	}

	@Override
	public String getName() {
		return user.getUsername();
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {

	}

	@Override
	public Collection<? super Authority> getAuthorities() {
		return user.getAuthorities();
	}

	public void setAuthorities(Collection<? super Authority> authorities) {
		this.authorities = authorities;
	}
}
