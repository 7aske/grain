package com._7aske.grain.core.reflect;

import java.lang.reflect.Method;

/**
 * ProxyInterceptorAbstractFactory is an interface for creating ProxyInterceptors for specific methods.
 */
public interface ProxyInterceptorAbstractFactory {
    /**
     * Gets the type of the object that the factory supports.
     *
     * @return the type of the object that the factory supports.
     * @param <T> the type of the object that the factory supports.
     */
    <T> Class<T> getDiscriminatorType();

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
