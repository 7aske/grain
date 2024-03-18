package com._7aske.grain.core.reflect;

import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.GrainNameResolver;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * GrainResolvingProxyInterceptor is a ProxyInterceptor that resolves grain references
 * and provides them to the proxy object but caching the result for future calls.
 */
public class GrainResolvingProxyInterceptor implements ProxyInterceptor {
    private final DependencyContainer container;
    private final GrainNameResolver grainNameResolver;
    private final Logger logger = LoggerFactory.getLogger(GrainResolvingProxyInterceptor.class);

    public GrainResolvingProxyInterceptor(DependencyContainer container, GrainNameResolver grainNameResolver) {
        this.container = container;
        this.grainNameResolver = grainNameResolver;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object intercept(Object self,
                            Method method,
                            Object[] args,
                            Method superMethod) throws Throwable {
        logger.debug("Intercepting method " + method.getName());
        String name = grainNameResolver.resolveReferenceName(method);

        Optional<?> grain = (name == null)
                ? container.getOptionalGrain(method.getReturnType())
                : container.getOptionalGrain(name);

        if (grain.isEmpty()) {
            return superMethod.invoke(self, args);
        }

        Object actualGrain = grain.get();
        // This is the case when we are "overriding" a grain using a @Grain
        // annotated method
        if (Arrays.stream(method.getParameters()).anyMatch(p -> p.getType().isAssignableFrom(actualGrain.getClass()))) {
            return superMethod.invoke(self, args);
        }

        return actualGrain;
    }
}
