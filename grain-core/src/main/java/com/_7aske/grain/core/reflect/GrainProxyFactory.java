package com._7aske.grain.core.reflect;

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
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

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
            throw new GrainReflectionException("Failed to create proxy for interfaces " + Arrays.toString(interfaces), e);
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

            return newClazz.getConstructor(paramTypes).newInstance(args);
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create proxy for class " + clazz.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Object createProxyFromRegistry(Class<? super T> clazz,
                                              Class<?>[] paramTypes,
                                              Object[] args,
                                              ProxyInterceptorAbstractFactoryRegistry registry) {
        logger.debug("Creating cache proxy for class " + clazz.getName());
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : registry.getSupportedAnnotations()) {
                if (method.isAnnotationPresent(annotation)) {
                    ProxyInterceptor interceptor = registry.getFactory(annotation).create(method);
                    byteBuddy = byteBuddy.method(ElementMatchers.is(method))
                            .intercept(MethodDelegation.to(
                                    ProxyInterceptorWrapper.wrap(interceptor)))
                            .annotateMethod(method.getDeclaredAnnotations());
                }
            }
        }

        try (DynamicType.Unloaded<T> unloaded = (DynamicType.Unloaded<T>) byteBuddy.make()) {
            Class<T> newClazz = (Class<T>) unloaded
                    .load(CLASS_LOADER, ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return newClazz.getConstructor(paramTypes).newInstance(args);
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create cache proxy for class " + clazz.getName(), e);
        }
    }
}
