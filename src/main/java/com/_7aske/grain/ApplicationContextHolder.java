package com._7aske.grain;

import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.exception.GrainRuntimeException;

// @Warning This class is a @Temporary hack to allow classes that unavailable
// for dependency injection to access application context.
public class ApplicationContextHolder {
	private static ApplicationContext applicationContext = null;

	private ApplicationContextHolder() {
	}

	static void setContext(ApplicationContext applicationContext) {
		if (ApplicationContextHolder.applicationContext != null && applicationContext != null)
			throw new GrainRuntimeException("Called set on non-null context");
		ApplicationContextHolder.applicationContext = applicationContext;
	}

	public static ApplicationContext getContext() {
		if (applicationContext == null)
			throw new GrainRuntimeException("Unable to get context");
		return applicationContext;
	}
}
