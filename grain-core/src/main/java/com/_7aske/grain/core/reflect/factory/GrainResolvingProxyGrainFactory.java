package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.*;
import com._7aske.grain.core.reflect.GrainResolvingProxyInterceptor;
import com._7aske.grain.core.reflect.ProxyInterceptor;
import com._7aske.grain.core.reflect.ProxyInterceptorWrapper;
import com._7aske.grain.exception.GrainReflectionException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.Method;

@Grain
public class GrainResolvingProxyGrainFactory implements GrainFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final Logger logger = LoggerFactory.getLogger(GrainResolvingProxyGrainFactory.class);
    private final DependencyContainer dependencyContainer;
    private final GrainNameResolver grainNameResolver;

    public GrainResolvingProxyGrainFactory(DependencyContainer dependencyContainer) {
        this.dependencyContainer = dependencyContainer;
        this.grainNameResolver = GrainNameResolver.getDefault();
    }

    @Override
    public int getOrder() {
        return Order.HIGHEST_PRECEDENCE + 100;
    }

    @Override
    public boolean supports(Injectable dependency) {
        return dependency.hasGrainMethodDependencies();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Injectable dependency, Object[] args) {
        Class<?> clazz = dependency.getType();
        logger.debug("Creating proxy for class " + clazz.getName());
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Grain.class)) {
                continue;
            }

            ProxyInterceptor interceptor = new GrainResolvingProxyInterceptor(dependencyContainer, grainNameResolver);
            byteBuddy = byteBuddy.define(method)
                    .intercept(MethodDelegation.to(
                            ProxyInterceptorWrapper.wrap(interceptor)))
                    .annotateMethod(method.getDeclaredAnnotations());
        }

        try (DynamicType.Unloaded<T> unloaded = (DynamicType.Unloaded<T>) byteBuddy.make()) {
            Class<T> newClazz = (Class<T>) unloaded
                    .load(CLASS_LOADER, ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return newClazz.getConstructor(dependency.getConstructor().getParameterTypes()).newInstance(args);
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create proxy for class " + clazz.getName(), e);
        }
    }
}
