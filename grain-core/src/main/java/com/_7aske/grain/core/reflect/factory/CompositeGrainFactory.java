package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Injectable;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.util.By;

import java.util.List;
import java.util.PriorityQueue;

@Grain
@Order(Order.HIGHEST_PRECEDENCE)
public class CompositeGrainFactory implements GrainFactory {
    private final PriorityQueue<GrainFactory> factories;

    public CompositeGrainFactory(List<GrainFactory> factoryList) {
        factories = new PriorityQueue<>(By.order());
        factories.addAll(factoryList);
    }

    @Override
    public boolean supports(Injectable dependency) {
        return true;
    }

    @Override
    public <T> T create(Injectable dependency, Object[] args) {
        int supports = 0;
        int lastSupports = 0;
        int index = 0;
        List<GrainFactory> factories = this.factories.stream().toList();
        for (GrainFactory factory : factories) {
            if (factory.supports(dependency)) {
                supports++;
                lastSupports = index++;
            }
        }
        if (supports == 1) {
            return factories.get(lastSupports).create(dependency, args);
        } else if (supports > 1) {
            throw new GrainInitializationException("Multiple factories support dependency: " + dependency);
        }

        throw new GrainInitializationException("No suitable factory found for dependency: " + dependency);
    }

    @Override
    public int getOrder() {
        return Order.HIGHEST_PRECEDENCE;
    }
}
