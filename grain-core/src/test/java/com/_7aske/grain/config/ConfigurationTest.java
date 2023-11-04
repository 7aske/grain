package com._7aske.grain.config;

import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
	public static final class TestApplication extends GrainApp {

	}

	@Grain
	public static final class TestClass {
		@Inject
		public Configuration configuration;

		public void changeConfiguration() {
			configuration.set("grain.server.host", "127.0.0.1");
		}
	}

	@Grain
	public static final class TestClass2 {
		@Inject
		public Configuration configuration;
	}

	@Test
	void testConfiguration_isInjected() {
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApplication.class.getPackageName());
		TestClass testObject = applicationContext.getGrain(TestClass.class);
		Assertions.assertEquals("0.0.0.0", testObject.configuration.get("grain.server.host"));
	}

	@Test
	void testConfiguration_changeOfConfigurationPropagates() {
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApplication.class.getPackageName());
		TestClass testObject = applicationContext.getGrain(TestClass.class);
		testObject.changeConfiguration();
		TestClass2 testObject2 = applicationContext.getGrain(TestClass2.class);
		Assertions.assertEquals("127.0.0.1", testObject2.configuration.get("grain.server.host"));
	}
}