package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.User;
import com._7aske.grain.security.UsernameAndPasswordAuthentication;
import com._7aske.grain.security.crypto.PasswordEncoder;
import com._7aske.grain.security.exception.*;
import com._7aske.grain.security.exception.GrainSecurityException;
import com._7aske.grain.security.service.UserService;

// @Incomplete
@Grain
public class AuthenticationManagerImpl implements AuthenticationManager {
	@Inject
	private UserService userService;
	@Inject
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws GrainSecurityException {
		String username = authentication.getName();
		Object password = authentication.getCredentials();

		User user = userService.findByUsername(username);

		if (user == null)
			throw new UserNotFoundException("User not found");

		if (!user.isEnabled())
			throw new UserDisabledException("User is disabled");

		if (!passwordEncoder.matches((String) password, (String) user.getPassword()))
			throw new InvalidCredentialsException("Invalid credentials");

		// @Refactor can possibly return empty token instead of throwing
		if (user.isCredentialsExpired())
			throw new CredentialsExpiredException("Credentials expired");

		return new UsernameAndPasswordAuthentication(username, password, user.getAuthorities());
	}
}
