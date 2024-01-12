package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainReflectionException;
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
		// Non-default method invocations should
		assertThrows(GrainReflectionException.class, () -> testDefault.get().sayGoodbye("7aske"));
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

		@Inject(annotatedBy = TestAnnotation.class)
		private TestAnnotated testAnnotatedField;

		// Injecting by annotation (erase the info about the type by trying to inject
		// a plain object)
		public TestAnnotatedRunner(@Inject(annotatedBy = TestAnnotation.class) Object testAnnotated) {
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

	public static class ToBeInjected {

	}

	@Grain
	public static class TestOptionalInject {
		@Inject(required = false)
		private ToBeInjected toBeInjected;
	}

	@Test
	void test_injectOptional() {
		grainInjector.inject(TestOptionalInject.class);
		TestOptionalInject testOptionalInject = grainInjector.getContainer().getGrain(TestOptionalInject.class);
		assertNotNull(testOptionalInject);
		assertNull(testOptionalInject.toBeInjected);
	}

	@Grain
	public static class Writer {
		private final StringBuilder builder = new StringBuilder();

		public void write(String string) {
			builder.append(string);
		}
	}

	@Grain
	@Order(3)
	public static class TestOrder1 {
		private final Writer writer;
		TestOrder1(Writer writer) {
			this.writer = writer;
			writer.write("TestOrder1 ");
		}

		@AfterInit
		public void test() {
			writer.write("TestOrder1AfterInit ");
		}

		@AfterInit
		@Order(Order.HIGHEST_PRECEDENCE)
		public void test1() {
			writer.write("TestOrder1AfterInit1 ");
		}
	}

	@Grain
	@Order(2)
	public static class TestOrder2 {
		TestOrder2(Writer writer) {
			writer.write("TestOrder2 ");
		}
	}

	@Grain
	@Order(1)
	public static class TestOrder3 {
		TestOrder3(Writer writer) {
			writer.write("TestOrder3 ");
		}
	}

	@Grain
	@Order(Order.HIGHEST_PRECEDENCE)
	public static class TestOrder4 {
		TestOrder4(Writer writer) {
			writer.write("TestOrder4 ");
		}
	}

	@Test
	void test_order() {
		grainInjector.inject(Set.of(TestOrder1.class, TestOrder2.class, TestOrder3.class, TestOrder4.class, Writer.class));
		DependencyContainerImpl container = (DependencyContainerImpl) grainInjector.getContainer();
		Writer writer = container.getGrain(Writer.class);
		assertEquals("TestOrder4 TestOrder3 TestOrder2 TestOrder1 TestOrder1AfterInit1 TestOrder1AfterInit ", writer.builder.toString());
	}
}