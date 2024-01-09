package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.Optional;
import java.util.Set;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.*;

class GrainInjectorTest {
	GrainInjector grainInjector;

	@BeforeEach
	void setUp() {
		Configuration configuration = Configuration.createDefault();
		grainInjector = new GrainInjector(configuration);
	}

	@Grain
	public interface TestDefault {
		default String sayHello(String name) {
			return "Hello " + name;
		}

		String sayGoodbye(String name);
	}


	@Test
	void inject() {
		grainInjector.inject(TestDefault.class);
		Optional<TestDefault> testDefault = grainInjector.getContainer().getOptionalGrain(TestDefault.class);
		assertTrue(testDefault.isPresent());
		assertEquals("Hello 7aske", testDefault.get().sayHello("7aske"));
		// Non-default method invocations should silently fail and return null.
		assertNull(testDefault.get().sayGoodbye("7aske"));
	}

	@Retention(RUNTIME)
	public @interface TestAnnotation {
	}

	@Grain
	@TestAnnotation
	public static class TestAnnotated {
		public String sayHello(String name) {
			return "Hello " + name;
		}
	}

	@Grain
	public static class TestAnnotatedRunner {
		private final TestAnnotated testAnnotated;

		@Inject
		@AnnotatedBy(TestAnnotation.class)
		private TestAnnotated testAnnotatedField;

		// Injecting by annotation (erase the info about the type by trying to inject
		// a plain object.
		public TestAnnotatedRunner(@AnnotatedBy(TestAnnotation.class) Object testAnnotated) {
			this.testAnnotated = (TestAnnotated) testAnnotated;
		}

        public String run(String name) {
			return testAnnotated.sayHello(name);
		}

		public String runField(String name) {
			return testAnnotatedField.sayHello(name);
		}
	}

	@Test
	void injectAnnotated() {
		grainInjector.inject(Set.of(TestAnnotatedRunner.class, TestAnnotated.class));
		Optional<TestAnnotatedRunner> testAnnotated = grainInjector.getContainer().getOptionalGrain(TestAnnotatedRunner.class);
		assertTrue(testAnnotated.isPresent());
		assertEquals("Hello 7aske", testAnnotated.get().run("7aske"));
		assertEquals("Hello 7askeField", testAnnotated.get().runField("7askeField"));
	}
}