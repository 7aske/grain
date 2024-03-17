package com._7aske.grain.core.reflect;

import com._7aske.grain.core.cache.*;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public abstract class CacheAwareProxyInterceptor {
    protected final Cache cache;
    protected final CacheKeyGenerator cacheKeyGenerator;

    protected CacheAwareProxyInterceptor(Cache cache, CacheKeyGenerator cacheKeyGenerator) {
        this.cache = cache;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @RuntimeType
    public abstract Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable;

    protected CacheKey generateCacheKey(Method method, Object... args) {
        return CacheKeyResolver.resolveCacheKey(cacheKeyGenerator, method, args);
    }

    protected boolean evaluateCondition(Method method, Object... args) {
        return CacheConditionEvaluator.evaluateCondition(method, args);
    }
}
