package com._7aske.grain.core.reflect;

import com._7aske.grain.core.cache.Cache;
import com._7aske.grain.core.cache.CacheKeyGenerator;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public class CacheUpdatingProxyInterceptor extends CacheAwareProxyInterceptor {
    public CacheUpdatingProxyInterceptor(Cache cache, CacheKeyGenerator cacheKeyGenerator) {
        super(cache, cacheKeyGenerator);
    }

    @Override
    @RuntimeType
    public Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable {
        return cache.put(
                generateCacheKey(method, args),
                superMethod.invoke(self, args)
        );
    }
}
