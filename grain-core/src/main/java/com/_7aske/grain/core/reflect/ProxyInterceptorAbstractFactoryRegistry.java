package com._7aske.grain.core.reflect;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registry containing all ProxyInterceptorAbstractFactory instances.
 */
@Grain
@Order(Order.HIGHEST_PRECEDENCE)
public class ProxyInterceptorAbstractFactoryRegistry {
    private final Map<Class<? extends Annotation>, ProxyInterceptorAbstractFactory> annotationFactories;

    public ProxyInterceptorAbstractFactoryRegistry(List<ProxyInterceptorAbstractFactory> factoryList) {
        annotationFactories = new HashMap<>();
        for (ProxyInterceptorAbstractFactory factory : factoryList) {
            if (factory instanceof AnnotationProxyInterceptorAbstractFactory annotationFactory) {
                annotationFactories.put(annotationFactory.getAnnotation(), annotationFactory);
            }
        }
    }

    /**
     * Returns all supported annotations.
     *
     * @return a set of supported annotations.
     */
    public Set<Class<? extends Annotation>> getSupportedAnnotations() {
        return annotationFactories.keySet();
    }

    /**
     * Returns a factory for the given annotation.
     *
     * @param annotation the annotation to get the factory for.
     * @return the factory for the given annotation.
     */
    public ProxyInterceptorAbstractFactory getFactory(Class<? extends Annotation> annotation) {
        return annotationFactories.get(annotation);
    }
}
