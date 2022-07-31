package com._7aske.grain.security.authentication;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.security.exception.AuthenticationFailedException;
import com._7aske.grain.security.exception.GrainSecurityException;

// @Incomplete
@Grain
public class AuthorizationManagerImpl implements AuthorizationManager {

	@Override
	public Authentication authorize(Authentication authentication) throws GrainSecurityException {
		if (authentication.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
			throw new AuthenticationFailedException();
		}

		return authentication;
	}
}
