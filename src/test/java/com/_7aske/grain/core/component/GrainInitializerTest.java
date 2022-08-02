package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class GrainInitializerTest {
	BetterGrainInitializer grainInitializer;

	@BeforeEach
	void setUp() {
		grainInitializer = new BetterGrainInitializer(Configuration.createDefault());
	}

	interface TestGrain {

	}

	@Grain
	static class TestGrainImpl implements TestGrain {
		public TestGrainImpl(TestDependency testDependency) {
		}
	}

	@Grain
	static class TestDependency {
		public TestDependency(TestGrain testGrain) {
		}
	}

	@Test
	void initialize() {
		grainInitializer.initialize(Set.of(TestGrainImpl.class, TestDependency.class));
	}
}