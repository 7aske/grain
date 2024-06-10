package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Injectable;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.reflect.ReflectionUtil;

@Grain
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultGrainFactory implements GrainFactory {

    @Override
    public boolean supports(Injectable dependency) {
        return !dependency.isInterface();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Injectable dependency, Object[] args) {
        if (dependency.isGrainMethodDependency() || dependency.isInterface()) {
            throw new UnsupportedOperationException("Dependency is not supported by this factory");
        }

        return (T) ReflectionUtil.newInstance(dependency.getConstructor(), args);
    }

    @Override
    public int getOrder() {
        return Order.LOWEST_PRECEDENCE;
    }
}
