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
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public class GrainProxyFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private final DependencyContainer dependencyContainer;
    private final GrainNameResolver grainNameResolver;
    private final Logger logger = LoggerFactory.getLogger(GrainProxyFactory.class);

    public GrainProxyFactory(DependencyContainer dependencyContainer, GrainNameResolver grainNameResolver) {
        this.dependencyContainer = dependencyContainer;
        this.grainNameResolver = grainNameResolver;
    }

    public <T> T createInterfaceProxy(Class<?> iface) {
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(Object.class)
                .implement(iface);

        for (Method method : iface.getDeclaredMethods()) {
            if (method.isDefault()) {
                byteBuddy = byteBuddy.define(method)
                        .intercept(SuperMethodCall.INSTANCE)
                        .annotateMethod(method.getDeclaredAnnotations());
            }
        }

        try (DynamicType.Unloaded<?> unloaded = byteBuddy.make()) {

            return (T) unloaded.load(CLASS_LOADER)
                    .getLoaded()
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create proxy for interface " + iface, e);
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
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            Optional<ProxyInterceptorAbstractFactory> factoryOptional = registry.getFactory(clazz);
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

            return newClazz.getConstructor(paramTypes).newInstance(args);
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create cache proxy for class " + clazz.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Object createMethodProxyFromRegistry(Class<? super T> clazz,
                                                    Class<?>[] paramTypes,
                                                    Object[] args,
                                                    ProxyInterceptorAbstractFactoryRegistry registry) {
        logger.debug("Creating proxy for class " + clazz.getName());
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                Optional<ProxyInterceptorAbstractFactory> factoryOptional = registry.getFactory(annotation.getClass());
                if (factoryOptional.isEmpty()) {
                    continue;
                }

                ProxyInterceptor interceptor = factoryOptional.get().create(method);
                byteBuddy = byteBuddy.method(ElementMatchers.is(method))
                        .intercept(MethodDelegation.to(ProxyInterceptorWrapper.wrap(interceptor)))
                        .annotateMethod(method.getDeclaredAnnotations());
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
