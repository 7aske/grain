package com._7aske.grain.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.orm.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GrainRegistryTest {
	interface TestService {
		void doSomething();
	}

	@Grain
	static class TestServiceImpl implements TestService {
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
		registry.registerGrains(GrainApp.class.getPackageName());
		TestController controller = registry.getGrain(TestController.class);
		assertDoesNotThrow(controller::doSomething);
	}
}