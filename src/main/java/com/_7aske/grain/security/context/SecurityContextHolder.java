package com._7aske.grain.security.context;

public class SecurityContextHolder {
	private static final SecurityContextHolderStrategy holderStrategy = new ThreadLocalSecurityContextHolderStrategy();

	public static SecurityContext getContext() {
		if (holderStrategy.getContext() == null)
			holderStrategy.setContext(holderStrategy.crateDefaultContext());
		return holderStrategy.getContext();
	}

	public static void setContext(SecurityContext context) {
		holderStrategy.setContext(context);
	}
}
