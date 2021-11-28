package com._7aske.grain.security.authentication;

import com._7aske.grain.security.Authentication;

public interface AuthorizationManager {
	Authentication authorize(Authentication authentication) throws SecurityException;
}
