package com._7aske.grain.util;

import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.GrainNameResolver;
import com._7aske.grain.exception.GrainReflectionException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class GrainProxyFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private final DependencyContainer dependencyContainer;
    private final GrainNameResolver grainNameResolver;
    private final Logger logger = LoggerFactory.getLogger(GrainProxyFactory.class);

    public GrainProxyFactory(DependencyContainer dependencyContainer, GrainNameResolver grainNameResolver) {
        this.dependencyContainer = dependencyContainer;
        this.grainNameResolver = grainNameResolver;
    }

    public static <T> T createInterfaceProxy(Class<?>... interfaces) {
        try (DynamicType.Unloaded<?> unloaded = new ByteBuddy()
                .subclass(Object.class)
                .implement(interfaces)
                .make()) {

            return (T) unloaded.load(CLASS_LOADER)
                    .getLoaded()
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new GrainReflectionException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Object create(Class<? super T> clazz, Class<?>[] paramTypes, Object[] args) {
        logger.debug("Creating proxy for class " + clazz.getName());
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Grain.class)) {
                continue;
            }

            byteBuddy = byteBuddy.define(method)
                    .intercept(MethodDelegation.to(new GrainResolvingProxyInterceptor(dependencyContainer, grainNameResolver)))
                    .annotateMethod(method.getDeclaredAnnotations());
        }

        try (DynamicType.Unloaded<T> unloaded = (DynamicType.Unloaded<T>) byteBuddy.make()) {
            Class<T> newClazz = (Class<T>) unloaded
                    .load(CLASS_LOADER, ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            Constructor<T> constructor = newClazz.getConstructor(paramTypes);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create proxy for class " + clazz.getName(), e);
        }
    }
}
