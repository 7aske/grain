package com._7aske.grain.core.component;

import com._7aske.grain.gtl.interpreter.Interpreter;

import java.lang.annotation.Annotation;

/**
 * Abstract class for condition evaluators. Conditional annotations must have
 * an evaluator class defined, and it must extend this class to provide a type-safe
 * interface for evaluating conditions.
 *
 * @param <T> Annotation type.
 *
 * @see Conditional
 */
public abstract class AbstractConditionEvaluator<T extends Annotation> {
    private final Class<T> type;
    protected AbstractConditionEvaluator(Class<T> type){
        this.type = type;
    }

    /**
     * Internal method to evaluate the condition and forward the type-casted annotation.
     *
     * @param annotation The annotation instance.
     * @param injectable The injectable instance that the condition is being evaluated for.
     * @param container The dependency container containing the dependencies.
     * @param interpreter The interpreter allowing for dynamic evaluation of expressions.
     * @return True if the condition is met, false otherwise.
     */
    public boolean doEvaluate(Annotation annotation, Injectable injectable, DependencyContainer container, Interpreter interpreter){
        return evaluate(type.cast(annotation), injectable, container, interpreter);
    }

    /**
     * Evaluate the condition.
     *
     * @param annotation The annotation instance.
     * @param injectable The injectable instance that the condition is being evaluated for.
     * @param container The dependency container containing the dependencies.
     * @param interpreter The interpreter allowing for dynamic evaluation of expressions.
     * @return True if the condition is met, false otherwise.
     */
    public abstract boolean evaluate(T annotation, Injectable injectable, DependencyContainer container, Interpreter interpreter);
}
