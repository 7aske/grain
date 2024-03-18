package com._7aske.grain.core.reflect;

import java.lang.reflect.Method;

/**
 * ProxyInterceptor is an interface for intercepting method calls on a proxy object.
 * It is used to provide custom behavior for method calls on a proxy object.
 */
public interface ProxyInterceptor {
    /**
     * Intercepts a method call on a proxy object.
     *
     * @param self the proxy object.
     * @param method the method being called.
     * @param args the arguments passed to the method.
     * @param superMethod method of the super class that the proxy class is extending.
     * @return the result of the method call.
     * @throws Throwable any exception that occurs during the method/super call.
     */
    Object intercept(Object self, Method method, Object[] args, Method superMethod) throws Throwable;
}
