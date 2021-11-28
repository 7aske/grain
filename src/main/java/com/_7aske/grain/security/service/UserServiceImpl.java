package com._7aske.grain.security.service;

import com._7aske.grain.component.AfterInit;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.component.Inject;
import com._7aske.grain.exception.GrainMultipleImplementationsException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.BasicUser;
import com._7aske.grain.security.User;
import com._7aske.grain.security.crypto.PasswordEncoder;
import com._7aske.grain.security.exception.UserNotFoundException;

import java.util.Collections;
import java.util.UUID;

@Grain
public class UserServiceImpl implements UserService {
	private User DEFAULT_USER;
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Inject
	private GrainRegistry grainRegistry;
	@Inject
	private PasswordEncoder passwordEncoder;

	@AfterInit
	public void setup() {
		try {
			grainRegistry.getGrain(UserService.class);
			String username = "root";
			String password = UUID.randomUUID().toString();
			DEFAULT_USER = new BasicUser(username, passwordEncoder.encode(password), Collections.emptyList());
			logger.info("Created default user with username: {} and password: {}", username, password);
		} catch (GrainMultipleImplementationsException ex) {
			// Ignored because we just need to check if this is the only implementation.
			// If it is we print the default user. Otherwise, not.
		}
	}

	@Override
	public User findByUsername(String username) throws UserNotFoundException {
		return DEFAULT_USER;
	}
}
