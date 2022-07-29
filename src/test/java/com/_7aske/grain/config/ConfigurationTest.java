package com._7aske.grain.config;

import com._7aske.grain.GrainApp;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
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
			configuration.setHost("127.0.0.1");
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
		TestClass testObject = applicationContext.getGrainRegistry().getGrain(TestClass.class);
		Assertions.assertEquals("0.0.0.0", testObject.configuration.getHost());
	}

	@Test
	void testConfiguration_changeOfConfigurationPropagates() {
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApplication.class.getPackageName());
		TestClass testObject = applicationContext.getGrainRegistry().getGrain(TestClass.class);
		testObject.changeConfiguration();
		TestClass2 testObject2 = applicationContext.getGrainRegistry().getGrain(TestClass2.class);
		Assertions.assertEquals("127.0.0.1", testObject2.configuration.getHost());
	}
}