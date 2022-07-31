package com._7aske.grain.security.authentication;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.exception.CredentialsExpiredException;
import com._7aske.grain.security.exception.GrainSecurityException;

// @Refactor @Bug this should be used to validated tokens and session after initial
// extraction of the SessionToke from the request. For now I don't think there
// is a mechanism that for example handles expired tokens.
@Grain
public class AuthenticationManagerImpl implements AuthenticationManager {
	@Override
	public Authentication authenticate(Authentication authentication) throws GrainSecurityException {
		if (!authentication.isAuthenticated())
			throw new CredentialsExpiredException("Session expired");
		return authentication;
	}
}
