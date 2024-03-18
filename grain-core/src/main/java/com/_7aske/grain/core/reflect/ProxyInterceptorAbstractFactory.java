package com._7aske.grain.core.reflect;

import java.lang.reflect.Method;

/**
 * ProxyInterceptorAbstractFactory is an interface for creating ProxyInterceptors for specific methods.
 */
public interface ProxyInterceptorAbstractFactory {
    /**
     * Checks if the factory supports the given object.
     *
     * @param object the object to check.
     * @return true if the factory supports the object, false otherwise.
     */
    boolean supports(Object object);

    /**
     * Creates a ProxyInterceptor for the given method.
     *
     * @param method the method to create the interceptor for.
     * @return the created interceptor.
     */
    ProxyInterceptor create(Method method);
}
