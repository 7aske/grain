package com._7aske.grain.security.context;

import com._7aske.grain.security.exception.GrainSecurityNullContextException;

class ThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
	private final ThreadLocal<SecurityContext> securityContext = new ThreadLocal<>();

	@Override
	public SecurityContext getContext() {
		return securityContext.get();
	}

	@Override
	public void setContext(SecurityContext securityContext) {
		if (securityContext == null)
			throw new GrainSecurityNullContextException();
		this.securityContext.set(securityContext);
	}

	@Override
	public SecurityContext createDefaultContext() {
		return new SecurityContextImpl();
	}

}
