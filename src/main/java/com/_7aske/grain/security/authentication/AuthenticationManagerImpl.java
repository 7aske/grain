package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Grain;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.exception.CredentialsExpiredException;
import com._7aske.grain.security.exception.GrainSecurityException;

@Grain
public class AuthenticationManagerImpl implements AuthenticationManager {
	@Override
	public Authentication authenticate(Authentication authentication) throws GrainSecurityException {
		if (!authentication.isAuthenticated())
			throw new CredentialsExpiredException("Session expired");
		return authentication;
	}
}
