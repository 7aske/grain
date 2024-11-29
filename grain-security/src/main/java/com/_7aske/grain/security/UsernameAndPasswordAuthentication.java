package com._7aske.grain.security;

import java.util.Collection;

public class UsernameAndPasswordAuthentication implements Authentication {
	private String username;
	private Object credentials;
	private boolean authenticated;
	private Collection<? super Authority> authorities;

	private UsernameAndPasswordAuthentication() {
		authenticated = true;
	}

	public UsernameAndPasswordAuthentication(String username, Object credentials, Collection<? super Authority> authorities) {
		this.username = username;
		this.credentials = credentials;
		this.authorities = authorities;
	}

	public UsernameAndPasswordAuthentication(User user, Collection<? super Authority> authorities) {
		this();
		this.username = user.getUsername();
		this.credentials = user.getPassword();
		this.authorities = authorities;
	}

	public UsernameAndPasswordAuthentication(User user) {
		this();
		this.username = user.getUsername();
		this.credentials = user.getPassword();
		this.authorities = user.getAuthorities();
	}

	@Override
	public String getName() {
		return username;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	@Override
	public Collection<? super Authority> getAuthorities() {
		return authorities;
	}

	@Override
	public void setAuthorities(Collection<? super Authority> authorities) {
		this.authorities = authorities;
	}
}
