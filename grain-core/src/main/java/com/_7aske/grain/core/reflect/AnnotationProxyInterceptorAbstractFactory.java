package com._7aske.grain.core.reflect;

import java.lang.annotation.Annotation;

/**
 * AnnotationProxyInterceptorAbstractFactory is an interface for creating ProxyInterceptors for specific methods
 * that takes into the account annotation placed on the method.
 */
public interface AnnotationProxyInterceptorAbstractFactory extends ProxyInterceptorAbstractFactory {
    Class<? extends Annotation> getAnnotation();

    default boolean supports(Object annotation) {
        return getAnnotation().equals(annotation);
    }
}
