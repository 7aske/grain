package com._7aske.grain.security.service;

import com._7aske.grain.core.component.Default;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.security.User;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.exception.UserNotFoundException;

@Grain
@Default
public class UserServiceImpl implements UserService {
	@Inject
	private SecurityConfiguration configuration;

	@Override
	public User findByUsername(String username) throws UserNotFoundException {
		User user = configuration.getDefaultUser();
		if (user.getUsername().equals(username))
			return user;

		return null;
	}
}
