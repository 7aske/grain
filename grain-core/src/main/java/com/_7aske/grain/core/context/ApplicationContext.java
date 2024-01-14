package com._7aske.grain.core.context;

import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.configuration.Configuration;

public interface ApplicationContext extends DependencyContainer {
	Configuration getConfiguration();

	String getPackage();
}
