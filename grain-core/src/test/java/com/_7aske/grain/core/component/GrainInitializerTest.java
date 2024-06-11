package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.reflect.factory.CompositeGrainFactory;
import com._7aske.grain.core.reflect.factory.DefaultGrainFactory;
import com._7aske.grain.core.reflect.factory.GrainMethodGrainFactory;
import com._7aske.grain.core.reflect.factory.InterfaceGrainFactory;
import com._7aske.grain.exception.GrainInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GrainInitializerTest {
	GrainInjector grainInitializer;

	@BeforeEach
	void setUp() {
		grainInitializer = new GrainInjector(Configuration.createDefault());
		grainInitializer.inject(Set.of(CompositeGrainFactory.class, InterfaceGrainFactory.class, DefaultGrainFactory.class, GrainMethodGrainFactory.class));
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
		grainInitializer.inject(Set.of(GrainImpl.class, Component.class));
		DependencyContainer dependencies = grainInitializer.getContainer();
		Optional<Component> dependency = dependencies.getOptionalGrain(Component.class);
		assertTrue(dependency.isPresent());
		Component component = dependency.get();
		assertNotNull(component);
		assertEquals(2, component.grains.size());

		assertTrue(component.grains.stream().anyMatch(g -> g.id == 1));
		assertTrue(component.grains.stream().anyMatch(g -> g.id == 2));
	}
}