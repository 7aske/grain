package com._7aske.grain.core.component;

import com._7aske.grain.gtl.interpreter.Interpreter;
import com._7aske.grain.util.StringUtils;

import java.util.Arrays;

/**
 * Condition evaluator for {@link ConditionalOnMissingGrain}.
 */
public class OnMissingGrainCondition extends AbstractConditionEvaluator<ConditionalOnMissingGrain> {
    public OnMissingGrainCondition(Class<ConditionalOnMissingGrain> type) {
        super(type);
    }

    @Override
    public boolean evaluate(ConditionalOnMissingGrain annotation, Injectable injectable, DependencyContainer container, Interpreter interpreter) {
        if (!StringUtils.isBlank(annotation.name())) {
            return container.getByName(annotation.name())
                    .filter(d -> d != injectable)
                    .map(d -> d.evaluateCondition(container, interpreter))
                    .orElse(true);
        }

        return Arrays.stream(annotation.value())
                .noneMatch(c -> container.getListByClass(c)
                        .stream()
                        .filter(d -> d != injectable)
                        .anyMatch(d -> d.evaluateCondition(container, interpreter)));
    }
}
