package com._7aske.grain.core.reflect;

import com._7aske.grain.core.cache.Cache;
import com._7aske.grain.core.cache.CacheKey;
import com._7aske.grain.core.cache.CacheKeyGenerator;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public class CacheResolvingProxyInterceptor extends CacheAwareProxyInterceptor {
    public CacheResolvingProxyInterceptor(Cache cache, CacheKeyGenerator cacheKeyGenerator) {
        super(cache, cacheKeyGenerator);
    }

    @Override
    @RuntimeType
    public Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable {
        CacheKey key = generateCacheKey(method, args);
        if (cache.contains(key)) {
            return cache.get(key);
        }

        return cache.put(key, superMethod.invoke(self, args));
    }
}
