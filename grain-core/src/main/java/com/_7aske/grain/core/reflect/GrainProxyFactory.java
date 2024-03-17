package com._7aske.grain.core.reflect;

import com._7aske.grain.core.cache.Cache;
import com._7aske.grain.core.cache.CacheKeyGenerator;
import com._7aske.grain.core.cache.CacheManager;
import com._7aske.grain.core.cache.CacheNameResolver;
import com._7aske.grain.core.cache.annotation.CacheEvict;
import com._7aske.grain.core.cache.annotation.CachePut;
import com._7aske.grain.core.cache.annotation.Cacheable;
import com._7aske.grain.core.cache.annotation.meta.CacheAware;
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

            byteBuddy = byteBuddy.define(method)
                    .intercept(MethodDelegation.to(new GrainResolvingProxyInterceptor(dependencyContainer, grainNameResolver)))
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
    public <T> Object createCacheProxy(Class<? super T> clazz,
                                       Class<?>[] paramTypes,
                                       Object[] args,
                                       CacheManager cacheManager,
                                       CacheKeyGenerator cacheKeyGenerator) {
        logger.debug("Creating cache proxy for class " + clazz.getName());
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            if (!ReflectionUtil.isAnnotationPresent(method, CacheAware.class)) {
                continue;
            }

            String cacheName = CacheNameResolver.resolveCacheName(method);
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                logger.warn("Cache with name " + cacheName + " not found");
                cache = cacheManager.createCache(cacheName);
            }

            if (method.isAnnotationPresent(Cacheable.class)) {
                byteBuddy = byteBuddy.define(method)
                        .intercept(MethodDelegation.to(new CacheResolvingProxyInterceptor(cache, cacheKeyGenerator)))
                        .annotateMethod(method.getDeclaredAnnotations());
            } else if (method.isAnnotationPresent(CacheEvict.class)) {
                byteBuddy = byteBuddy.define(method)
                        .intercept(MethodDelegation.to(new CacheEvictingProxyInterceptor(cache, cacheKeyGenerator)))
                        .annotateMethod(method.getDeclaredAnnotations());
            } else if (method.isAnnotationPresent(CachePut.class)) {
                byteBuddy = byteBuddy.define(method)
                        .intercept(MethodDelegation.to(new CacheUpdatingProxyInterceptor(cache, cacheKeyGenerator)))
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
