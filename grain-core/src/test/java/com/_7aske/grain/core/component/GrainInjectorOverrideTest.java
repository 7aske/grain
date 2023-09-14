package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrainInjectorOverrideTest {
    GrainInjector grainInjector;

    public static final class TestApp {

        @Grain
        public Configuration configuration(Configuration configuration) {
            configuration.set("test", "test");
            return configuration;
        }
    }

    @BeforeEach
    void setUp() {
        Configuration configuration = Configuration.createDefault();
        grainInjector = new GrainInjector(configuration);
    }


    @Test
    void inject() {
        Set<Class<?>> toInject = Set.of(TestApp.class);
        grainInjector.inject(toInject);

        for (Injectable<?> aClass : grainInjector.getContainer().getAll()) {
            System.out.println(aClass.getType());
        }

        Collection<Configuration> configurations = grainInjector.getContainer().getGrains(Configuration.class);
        assertEquals(1, configurations.size());
        assertEquals("test", grainInjector.getContainer().getOptionalGrain(Configuration.class).get().get("test"));
    }
}
