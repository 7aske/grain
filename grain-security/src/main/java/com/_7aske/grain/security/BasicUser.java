package com._7aske.grain.security;

import java.util.ArrayList;
import java.util.Collection;

public class BasicUser implements User {
	private String username;
	private String password;
	// @Note do we want to set roles after object creation?
	private Collection<? super Authority> authorities;

	public BasicUser() {
		this.username = null;
		this.password = null;
		this.authorities = new ArrayList<>();
	}

	public BasicUser(String username, String password, Collection<? super Authority> authorities) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<? super Authority> authorities) {
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
	public Collection<? super Authority> getAuthorities() {
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
