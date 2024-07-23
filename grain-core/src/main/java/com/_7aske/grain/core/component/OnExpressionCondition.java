package com._7aske.grain.core.component;

import com._7aske.grain.gtl.interpreter.Interpreter;

/**
 * Condition evaluator for {@link ConditionalOnExpression}.
 */
public class OnExpressionCondition extends AbstractConditionEvaluator<ConditionalOnExpression> {
    public OnExpressionCondition(Class<ConditionalOnExpression> type) {
        super(type);
    }

    @Override
    public boolean evaluate(ConditionalOnExpression annotation, Injectable injectable, DependencyContainer container, Interpreter interpreter) {
        return Boolean.TRUE.equals(interpreter.evaluate(annotation.value()));
    }
}
