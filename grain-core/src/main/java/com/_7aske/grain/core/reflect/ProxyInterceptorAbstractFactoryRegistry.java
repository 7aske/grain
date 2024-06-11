package com._7aske.grain.core.reflect;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry containing all ProxyInterceptorAbstractFactory instances.
 */
@Grain
@Order(Order.HIGHEST_PRECEDENCE)
public class ProxyInterceptorAbstractFactoryRegistry {
    private final List<ProxyInterceptorAbstractFactory> factories;

    public ProxyInterceptorAbstractFactoryRegistry(List<ProxyInterceptorAbstractFactory> factoryList) {
        this.factories = factoryList;
    }

    public Optional<ProxyInterceptorAbstractFactory> getFactory(Object object) {
        return factories
                .stream()
                .filter(factory -> factory.supports(object))
                .findFirst();
    }


    public boolean supports(Object object) {
        return factories
                .stream()
                .anyMatch(factory -> factory.supports(object));
    }
}
