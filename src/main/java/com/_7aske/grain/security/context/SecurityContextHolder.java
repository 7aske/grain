package com._7aske.grain.security.context;

import com._7aske.grain.annotation.NotNull;

public class SecurityContextHolder {
	private static final SecurityContextHolderStrategy holderStrategy = new ThreadLocalSecurityContextHolderStrategy();

	private SecurityContextHolder() {
	}

	public static @NotNull SecurityContext getContext() {
		if (holderStrategy.getContext() == null)
			holderStrategy.setContext(holderStrategy.createDefaultContext());
		return holderStrategy.getContext();
	}

	public static void setContext(SecurityContext context) {
		holderStrategy.setContext(context);
	}
}
