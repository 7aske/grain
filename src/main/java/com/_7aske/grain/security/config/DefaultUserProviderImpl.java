package com._7aske.grain.security.config;

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
import com._7aske.grain.security.service.UserService;

import java.util.Collections;
import java.util.UUID;

@Grain
public class DefaultUserProviderImpl implements DefaultUserProvider {
	@Inject
	private GrainRegistry grainRegistry;
	@Inject
	private PasswordEncoder passwordEncoder;

	private Logger logger = LoggerFactory.getLogger(DefaultUserProviderImpl.class);

	private User user = null;

	@AfterInit
	public void setup() {
		try {
			grainRegistry.getGrain(UserService.class);
			String username = "root";
			String password = UUID.randomUUID().toString();
			logger.info("Created default user with username: {} and password: {}", username, password);
			user = new BasicUser(username, passwordEncoder.encode(password), Collections.emptyList());
		} catch (GrainMultipleImplementationsException ex) {
			// Ignored because we just need to check if this is the only implementation.
			// If it is we print the default user. Otherwise, not.
		}
	}

	@Override
	public User getUser() {
		return user;
	}
}
