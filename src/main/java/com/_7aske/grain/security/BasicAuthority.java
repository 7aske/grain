package com._7aske.grain.security;

/**
 * Default authority implementation.
 */
public class BasicAuthority implements Authority {
	private final String name;

	public BasicAuthority(String name) {
		this.name = name;
	}

	/**
	 * Gets the string identifier of the authority.
	 * @return authorization name.
	 */
	public String getName() {
		return name;
	}
}
