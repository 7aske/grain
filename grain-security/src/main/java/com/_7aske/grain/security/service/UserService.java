package com._7aske.grain.security.service;

import com._7aske.grain.security.User;
import com._7aske.grain.security.exception.UserNotFoundException;

/* Interface and default implementation of UserService. This should be overridden
 * by the user of the library. In order to provide ability to get the user from
 * whatever storage.
 */
public interface UserService {
	/**
	 * Returns the user object corresponding to the given username.
	 * @param username to search the users for.
	 * @return found user.
	 * @throws UserNotFoundException if the user is not found.
	 */
	User findByUsername(String username) throws UserNotFoundException;
}
