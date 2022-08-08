package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
}