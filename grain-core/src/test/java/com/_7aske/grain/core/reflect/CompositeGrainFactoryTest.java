package com._7aske.grain.core.reflect;

import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Injectable;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.core.reflect.factory.CompositeGrainFactory;
import com._7aske.grain.core.reflect.factory.GrainFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompositeGrainFactoryTest {

    @Grain
    public static class TestFactory implements GrainFactory {
        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean supports(Injectable dependency) {
            return dependency.isInterface();
        }

        @Override
        public <T> T create(Injectable dependency, Object[] args) {
            System.out.println("TestFactory.create");
            return null;
        }
    }

    public interface TestInterface {
    }
    
    @Test
    void create() {
        ApplicationContext applicationContext = new ApplicationContextImpl(GrainApp.class.getPackageName());
        Injectable dependency = new Injectable(TestInterface.class, null);

        assertNotNull(applicationContext.getGrain(CompositeGrainFactory.class).create(dependency, null));
    }
    

}