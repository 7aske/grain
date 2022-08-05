package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

	static class BeanImpl {
		public BeanImpl() {
		}
	}

	@Grain
	static class GrainImpl {
		public GrainImpl() {
		}

		@Grain
		public BeanImpl bean() {
			return new BeanImpl();
		}
	}

	@Grain
	static class Component {
		public Component(BeanImpl grain) {
		}
	}


	@Test
	void initialize_circular() {
		assertThrows(GrainInitializationException.class,
				() -> grainInitializer.initialize(Set.of(TestGrainImpl.class, TestDependency.class)));
	}

	@Test
	void initialize_missing() {
		assertThrows(GrainInitializationException.class,
				() -> grainInitializer.initialize(Set.of(TestDependency.class)));
	}

	@Test
	void initialize() {
		DependencyContainer dependencies = grainInitializer.initialize(Set.of(GrainImpl.class, Component.class));
		Optional<BetterDependency> dependency = dependencies.getByClass(GrainImpl.class);
		assertTrue(dependency.isPresent());
		assertNotNull(dependency.get().getInstance());
	}
}