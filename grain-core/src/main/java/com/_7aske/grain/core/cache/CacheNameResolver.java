package com._7aske.grain.core.cache;

import com._7aske.grain.core.cache.annotation.CacheEvict;
import com._7aske.grain.core.cache.annotation.CachePut;
import com._7aske.grain.core.cache.annotation.Cacheable;

import java.lang.reflect.Method;

public class CacheNameResolver {
    private CacheNameResolver() {}

    public static String resolveCacheName(Method method) {
        String value = null;
        if (method.isAnnotationPresent(Cacheable.class)) {
            value = method.getAnnotation(Cacheable.class).value();
        } else if (method.isAnnotationPresent(CachePut.class)) {
            value = method.getAnnotation(CachePut.class).value();
        } else if (method.isAnnotationPresent(CacheEvict.class)) {
            value = method.getAnnotation(CacheEvict.class).value();
        }
        if (value != null && !value.isEmpty()) {
            return value;
        }

        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
