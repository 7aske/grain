package com._7aske.grain.security;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BasicAuthority that = (BasicAuthority) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
