package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.*;
import com._7aske.grain.core.reflect.ReflectionUtil;

@Grain
public class GrainMethodGrainFactory implements GrainFactory {
    private final DependencyContainerImpl dependencyContainer;

    public GrainMethodGrainFactory(DependencyContainerImpl dependencyContainer) {
        this.dependencyContainer = dependencyContainer;
    }

    @Override
    public int getOrder() {
        return Order.HIGHEST_PRECEDENCE + 300;
    }

    @Override
    public boolean supports(Injectable dependency) {
        return dependency.isGrainMethodDependency();
    }

    @Override
    public <T> T create(Injectable dependency, Object[] args) {
        Injectable methodDependency = InjectableReference.of(dependency.getParentMethod())
                .resolve(dependencyContainer);
        Object result = ReflectionUtil.invokeMethod(dependency.getParentMethod(), dependency.getParent().getInstance(), args);
        methodDependency.setInstance(result);

        return (T) result;
    }
}
