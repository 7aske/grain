package com._7aske.grain.core.reflect;

import com._7aske.grain.core.cache.Cache;
import com._7aske.grain.core.cache.CacheKey;
import com._7aske.grain.core.cache.CacheKeyGenerator;
import com._7aske.grain.core.cache.annotation.CacheEvict;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public class CacheEvictingProxyInterceptor extends CacheAwareProxyInterceptor {
    public CacheEvictingProxyInterceptor(Cache cache, CacheKeyGenerator cacheKeyGenerator) {
        super(cache, cacheKeyGenerator);
    }

    @Override
    @RuntimeType
    public Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable {
        if (evaluateCondition(method, args)) {
            CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

            if (cacheEvict.allEntries()) {
                cache.clear();
            } else {
                cache.evict(generateCacheKey(method, args));
            }
        }


        return superMethod.invoke(self, args);
    }
}
