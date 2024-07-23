package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Evaluates the expression in the current configuration context of the Grain application.
 */
@Conditional(OnExpressionCondition.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ConditionalOnExpression {
	String value();
}
