package com._7aske.grain.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.context.ApplicationContextImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GrainRegistryTest {
	static final class TestApp extends GrainApp {

	}
	interface TestService {
		void doSomething();
	}

	@Grain
	static final class AfterInitService  {
		public String getSomething() {
			return "something";
		}
	}

	@Grain
	static class TestServiceImpl implements TestService {
		public String something = null;
		@Inject
		public AfterInitService afterInitService;

		@AfterInit
		public void afterInit() {
			something = afterInitService.getSomething();
		}

		@Override
		public void doSomething() {
			System.out.println("doSomething");
		}
	}

	@Grain
	static class TestController {
		private TestService testService;
		public TestController(TestService testService) {
			this.testService = testService;
		}

		void doSomething() {
			testService.doSomething();
		}
	}

	@Test
	void test_grainRegistry() {
		GrainRegistry registry = new GrainRegistry();
		// one of the manually added dependencies
		registry.registerGrain(Configuration.createDefault());
		registry.registerGrains(GrainApp.class.getPackageName());
		TestController controller = registry.getGrain(TestController.class);
		assertDoesNotThrow(controller::doSomething);
	}

	@Test
	void testAfterInit() {
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApp.class.getPackageName());
		TestServiceImpl testService = applicationContext.getGrainRegistry().getGrain(TestServiceImpl.class);
		assertEquals("something", testService.something);
	}
}