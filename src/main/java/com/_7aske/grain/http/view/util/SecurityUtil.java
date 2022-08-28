package com._7aske.grain.http.view.util;

import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.Authority;
import com._7aske.grain.security.context.SecurityContextHolder;

/**
 * Utility for {@link Authentication} based operations to be used in the view
 * layer.
 */
public class SecurityUtil {
	private SecurityUtil() {
	}

	/**
	 * Checks whether the user is authenticated and has a specific authority
	 * @param authority Authority we are looking for.
	 * @return True if the user is authenticated and has the provided authority.
	 */
	public static boolean hasAuthority(String authority) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) return false;
		return authentication.getAuthorities()
				.stream()
				.anyMatch(a -> ((Authority) a).getName().equals(authority));
	}
}