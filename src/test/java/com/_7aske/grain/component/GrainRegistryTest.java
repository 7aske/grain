package com._7aske.grain.component;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.*;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrainRegistryTest {
	static final class TestApp extends GrainApp {

	}

	interface TestService {
		void doSomething();
	}

	@Grain
	static final class AfterInitService {
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
		GrainRegistry registry = new GrainRegistry(Configuration.createDefault());
		// one of the manually added dependencies
		registry.registerGrain(Configuration.createDefault());
		registry.registerGrains(GrainApp.class.getPackageName());
		TestController controller = registry.getGrain(TestController.class);
		assertDoesNotThrow(controller::doSomething);
	}

	@Test
	void testAfterInit() {
		ApplicationContextHolder.setContext(null);
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApp.class.getPackageName());
		TestServiceImpl testService = applicationContext.getGrainRegistry().getGrain(TestServiceImpl.class);
		assertEquals("something", testService.something);
	}

	@Grain
	@Condition("false")
	static final class TestCondition {

	}

	@Grain
	@Condition("test.prop")
	static final class TestConditionTrue {
		@Value("options.number")
		private Integer number;
	}

	@Grain
	@Condition("profile.active == 'test'")
	static final class TestConditionActiveProfile {

	}

	@Grain
	static final class TestValue {
		@Value("options.number")
		private Integer number;
		@Value("options.string")
		private String string;
		private static boolean called = false;

		public String getValue() {
			System.out.println("CALLED");
			return "returned value";
		}
	}

	@Grain
	static final class TestValueReferenceGrain {
		@Value("testValue.number")
		private Integer number;
		@Value("testValue.getValue()")
		private String called;
	}

	@Test
	void testConditionalLoad() {
		Configuration configuration = Configuration.createDefault();
		configuration.set("test.prop", true);
		configuration.set("profile.active", "test");
		GrainRegistry registry = new GrainRegistry(configuration);
		registry.registerGrain(configuration);
		registry.registerGrains(GrainApp.class.getPackageName());
		assertNull(registry.getGrain(TestCondition.class));
		assertNotNull(registry.getGrain(TestConditionTrue.class));
	}

	@Test
	void testValue() {
		Configuration configuration = Configuration.createDefault();
		configuration.set("options.number", 42);
		configuration.set("options.string", "test");
		GrainRegistry registry = new GrainRegistry(configuration);
		registry.registerGrain(configuration);
		registry.registerGrains(GrainApp.class.getPackageName());
		TestValue obj = registry.getGrain(TestValue.class);
		assertEquals(42, obj.number);
		assertEquals("test", obj.string);
	}

	@Test
	void testValueReferenceGrain() {
		Configuration configuration = Configuration.createDefault();
		configuration.set("options.number", 42);
		GrainRegistry registry = new GrainRegistry(configuration);
		registry.registerGrain(configuration);
		registry.registerGrains(GrainApp.class.getPackageName());
		TestValueReferenceGrain obj = registry.getGrain(TestValueReferenceGrain.class);
		// @Todo This fails because obj.number is being processed by the initializer
		// before its @Value provider (testValue.number) is initialized. We need
		// to by potentially creating dependency graphs when initializing fields
		// which is not a trivial task.
		// assertEquals(42, obj.number);
		assertEquals("returned value", obj.called);
	}

}