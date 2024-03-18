package com._7aske.grain.core.reflect;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * ProxyInterceptorWrapper is a wrapper for ProxyInterceptors that allows ByteBuddy
 * to intercept method calls on a proxy object.
 */
public class ProxyInterceptorWrapper implements ProxyInterceptor {
    private final ProxyInterceptor target;

    protected ProxyInterceptorWrapper(ProxyInterceptor target) {
        this.target = target;
    }

    /**
     * Wraps the given interceptor with a ProxyInterceptorWrapper.
     *
     * @param interceptor the interceptor to wrap.
     * @return the wrapped interceptor.
     */
    public static ProxyInterceptor wrap(ProxyInterceptor interceptor) {
        return new ProxyInterceptorWrapper(interceptor);
    }

    /**
     * @inheritDoc
     */
    @Override
    @RuntimeType
    public Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable {
        return target.intercept(self, method, args, superMethod);
    }
}
