package com._7aske.grain.core.cache;

import com._7aske.grain.core.cache.annotation.CacheEvict;
import com._7aske.grain.core.cache.annotation.CachePut;
import com._7aske.grain.core.cache.annotation.Cacheable;
import com._7aske.grain.gtl.interpreter.Interpreter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CacheKeyResolver {
    private CacheKeyResolver() {}

    public static CacheKey resolveCacheKey(CacheKeyGenerator keyGenerator, Method method, Object... args) {
        String key = null;
        if (method.isAnnotationPresent(Cacheable.class)) {
            key = method.getAnnotation(Cacheable.class).key();
        } else if (method.isAnnotationPresent(CachePut.class)) {
            key = method.getAnnotation(CachePut.class).key();
        } else if (method.isAnnotationPresent(CacheEvict.class)) {
            key = method.getAnnotation(CacheEvict.class).key();
        }

        if (key != null && !key.isEmpty()) {
            Interpreter interpreter = new Interpreter();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; ++i) {
                interpreter.putSymbol(parameters[i].getName(), args[i]);
            }

            return keyGenerator.generate(interpreter.evaluate(key));
        }

        return keyGenerator.generate(args);
    }
}
