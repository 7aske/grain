package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.core.component.Injectable;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.reflect.ProxyInterceptor;
import com._7aske.grain.core.reflect.ProxyInterceptorAbstractFactory;
import com._7aske.grain.core.reflect.ProxyInterceptorAbstractFactoryRegistry;
import com._7aske.grain.core.reflect.ProxyInterceptorWrapper;
import com._7aske.grain.exception.GrainReflectionException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

@Grain
@Order(Order.HIGHEST_PRECEDENCE + 400)
public class ProxyRegistryGrainFactory implements GrainFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private final ProxyInterceptorAbstractFactoryRegistry proxyRegistry;

    public ProxyRegistryGrainFactory(@Inject(required = false) ProxyInterceptorAbstractFactoryRegistry proxyRegistry) {
        this.proxyRegistry = proxyRegistry;
    }

    @Override
    public int getOrder() {
        return Order.HIGHEST_PRECEDENCE + 400;
    }

    @Override
    public boolean supports(Injectable dependency) {
        return !Modifier.isFinal(dependency.getType().getModifiers()) && Optional.of(proxyRegistry)
                .map(registry -> registry.supports(dependency.getType()))
                .orElse(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Injectable dependency, Object[] args) {
        Class<T> clazz = (Class<T>) dependency.getType();
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            Optional<ProxyInterceptorAbstractFactory> factoryOptional = proxyRegistry.getFactory(method);
            if (factoryOptional.isEmpty()) {
                continue;
            }

            ProxyInterceptor interceptor = factoryOptional.get().create(method);
            byteBuddy = byteBuddy.method(ElementMatchers.is(method))
                    .intercept(MethodDelegation.to(ProxyInterceptorWrapper.wrap(interceptor)))
                    .annotateMethod(method.getDeclaredAnnotations());
        }

        try (DynamicType.Unloaded<T> unloaded = (DynamicType.Unloaded<T>) byteBuddy.make()) {
            Class<T> newClazz = (Class<T>) unloaded
                    .load(CLASS_LOADER, ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return newClazz.getConstructor(dependency.getConstructor().getParameterTypes()).newInstance(args);
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create cache proxy for class " + clazz.getName(), e);
        }
    }
}
