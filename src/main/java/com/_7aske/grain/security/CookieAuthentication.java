package com._7aske.grain.security;

import com._7aske.grain.http.session.Cookie;
import com._7aske.grain.http.session.SessionConstants;

import java.util.ArrayList;
import java.util.Collection;

public class CookieAuthentication implements Authentication {
	private String username;
	private Cookie credentials;
	private Collection<? super Authority> authorities;

	public CookieAuthentication(String username, Cookie credentials, Collection<? super Authority> authorities) {
		this.username = username;
		this.credentials = credentials;
		this.authorities = authorities;
	}
	public CookieAuthentication(Cookie credentials) {
		this.username = null;
		this.credentials = credentials;
		this.authorities = new ArrayList<>();
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
		return !credentials.isExpired();
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		credentials.setMaxAge(System.currentTimeMillis() / 1000 + SessionConstants.SESSION_DEFAULT_MAX_AGE);
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
