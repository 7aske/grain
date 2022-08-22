package com._7aske.grain.security.context;

/**
 * This interface describes how to manage setting and getting of context in
 * {@link SecurityContextHolder}.
 */
interface SecurityContextHolderStrategy {

	SecurityContext getContext();

	void setContext(SecurityContext securityContext);

	SecurityContext createDefaultContext();
}
