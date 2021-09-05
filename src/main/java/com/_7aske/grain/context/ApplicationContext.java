package com._7aske.grain.context;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

public interface ApplicationContext {
	GrainRegistry getGrainRegistry();
	StaticLocationsRegistry getStaticLocationsRegistry();
	String getPackage();
}
