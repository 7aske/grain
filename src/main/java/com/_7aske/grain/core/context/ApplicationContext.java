package com._7aske.grain.core.context;

import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.configuration.Configuration;

public interface ApplicationContext {
	DependencyContainer getDependencyContainer();

	Configuration getConfiguration();

	String getPackage();

	<T> T getGrain(Class<T> clazz);
}
