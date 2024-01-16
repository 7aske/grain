package com._7aske.grain.security.authentication;

import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.exception.GrainSecurityException;

public interface AuthorizationManager {
	Authentication authorize(Authentication authentication) throws GrainSecurityException;
}
