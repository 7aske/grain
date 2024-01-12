package com._7aske.grain.util;

import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.GrainNameResolver;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Optional;

public class GrainResolvingProxyInterceptor {
    private final DependencyContainer container;
    private final GrainNameResolver grainNameResolver;
    private final Logger logger = LoggerFactory.getLogger(GrainResolvingProxyInterceptor.class);

    public GrainResolvingProxyInterceptor(DependencyContainer container, GrainNameResolver grainNameResolver) {
        this.container = container;
        this.grainNameResolver = grainNameResolver;
    }

    @RuntimeType
    public Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable {
        logger.debug("Intercepting method " + method.getName());
        String name = grainNameResolver.resolveReferenceName(method);

        Optional<?> grain = (name == null)
                ? container.getOptionalGrain(method.getReturnType())
                : container.getOptionalGrain(name);

        if (grain.isPresent()) {
            return grain.get();
        }

        return superMethod.invoke(self, args);
    }
}
