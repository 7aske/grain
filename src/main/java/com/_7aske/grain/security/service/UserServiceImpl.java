package com._7aske.grain.security.service;

import com._7aske.grain.component.Default;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.security.User;
import com._7aske.grain.security.config.DefaultUserProvider;
import com._7aske.grain.security.exception.UserNotFoundException;

@Grain
@Default
public class UserServiceImpl implements UserService {
	@Inject
	private DefaultUserProvider defaultUserProvider;

	@Override
	public User findByUsername(String username) throws UserNotFoundException {
		return defaultUserProvider.getUser();
	}
}
