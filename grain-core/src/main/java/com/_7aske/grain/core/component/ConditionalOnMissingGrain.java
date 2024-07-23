package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Matches if a Grain bean with the specified type or name is NOT present.
 */
@Conditional(OnMissingGrainCondition.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ConditionalOnMissingGrain {
	Class<?>[] value() default {};

	String name() default "";
}
