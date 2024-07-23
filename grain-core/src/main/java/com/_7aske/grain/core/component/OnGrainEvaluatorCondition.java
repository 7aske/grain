package com._7aske.grain.core.component;

import com._7aske.grain.gtl.interpreter.Interpreter;

import java.util.Arrays;

/**
 * Condition evaluator for {@link ConditionalOnGrain}.
 */
public class OnGrainEvaluatorCondition extends AbstractConditionEvaluator<ConditionalOnGrain> {
    public OnGrainEvaluatorCondition(Class<ConditionalOnGrain> type) {
        super(type);
    }

    @Override
    public boolean evaluate(ConditionalOnGrain annotation, Injectable injectable, DependencyContainer container, Interpreter interpreter) {
        return Arrays.stream(annotation.value())
                .allMatch(c -> container.getOptionalGrain(c).isPresent());
    }
}
