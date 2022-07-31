package com._7aske.grain.core.context;

import com._7aske.grain.core.component.GrainRegistry;
import com._7aske.grain.core.configuration.Configuration;

public interface ApplicationContext {
	GrainRegistry getGrainRegistry();
	Configuration getConfiguration();
	String getPackage();
	<T> T getGrain(Class<T> clazz);
}
