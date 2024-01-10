package com._7aske.grain.core.component;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.util.ReflectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
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
    void inject() throws NoSuchMethodException {
        Set<Class<?>> toInject = Set.of(TestApp.class);
        grainInjector.inject(toInject);

        Object container = ReflectionUtil.getFieldValue(grainInjector, "container");
        Method getAll = (container.getClass()).getMethod("getAll");
        Collection<Injectable> injectables = (Collection<Injectable>) ReflectionUtil.invokeMethod(getAll, container);
        for (Injectable aClass : injectables) {
            System.out.println(aClass.getType());
        }

        Collection<Configuration> configurations = grainInjector.getContainer().getGrains(Configuration.class);
        assertEquals(1, configurations.size());
        assertEquals("test", grainInjector.getContainer().getOptionalGrain(Configuration.class).get().get("test"));
    }
}
