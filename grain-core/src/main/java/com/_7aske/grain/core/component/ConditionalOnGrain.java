package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Matches if a Grain bean with the specified type or name is present.
 */
@Conditional(OnGrainEvaluatorCondition.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ConditionalOnGrain {
	Class<?>[] value() default {};

	String name() default "";
}
