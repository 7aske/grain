package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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

	static class TestGrainImpl implements TestGrain {
		public TestGrainImpl(TestDependency testDependency) {
		}
	}

	static class TestDependency {
		public TestDependency(TestGrain testGrain) {
		}
	}

	static class BeanImpl {
		public int id;
		public BeanImpl(int id) {
			this.id = id;
		}
	}

	static class GrainImpl {
		public GrainImpl() {
		}

		@Grain
		public BeanImpl bean1() {
			return new BeanImpl(1);
		}

		@Grain
		public BeanImpl bean2() {
			return new BeanImpl(2);
		}
	}

	static class Component {
		public List<BeanImpl> grains;
		public Component(List<BeanImpl> grains) {
			this.grains = grains;
		}
	}


	@Test
	void initialize_circular() {
		assertThrows(GrainInitializationException.class,
				() -> grainInitializer.inject(Set.of(TestGrainImpl.class, TestDependency.class)));
	}

	@Test
	void initialize_missing() {
		assertThrows(GrainInitializationException.class,
				() -> grainInitializer.inject(Set.of(TestDependency.class)));
	}

	@Test
	void initialize() {
		DependencyContainer dependencies = grainInitializer.inject(Set.of(GrainImpl.class, Component.class));
		Optional<BetterDependency> dependency = dependencies.getByClass(Component.class);
		assertTrue(dependency.isPresent());
		Component component = dependency.get().getInstance();
		assertNotNull(component);
		assertEquals(2, component.grains.size());

		assertTrue(component.grains.stream().anyMatch(g -> g.id == 1));
		assertTrue(component.grains.stream().anyMatch(g -> g.id == 2));
	}
}