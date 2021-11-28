package com._7aske.grain.security.context;

import com._7aske.grain.security.Authentication;

public class SecurityContextImpl implements SecurityContext {
	private Authentication authentication;

	SecurityContextImpl() {
		this.authentication = null;
	}

	@Override
	public Authentication getAuthentication() {
		return this.authentication;
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
}
