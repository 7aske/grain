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
    private final Map<Class<?>, ProxyInterceptorAbstractFactory> factories;

    public ProxyInterceptorAbstractFactoryRegistry(List<ProxyInterceptorAbstractFactory> factoryList) {
        factories = new HashMap<>();
        for (ProxyInterceptorAbstractFactory factory : factoryList) {
            factories.put(factory.getDiscriminatorType(), factory);
        }
    }

    public Optional<ProxyInterceptorAbstractFactory> getFactory(Class<?> clazz) {
        return factories
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAssignableFrom(clazz))
                .map(Map.Entry::getValue)
                .findFirst();
    }


    public boolean supports(Class<?> clazz) {
        return getFactory(clazz).isPresent();
    }
}
