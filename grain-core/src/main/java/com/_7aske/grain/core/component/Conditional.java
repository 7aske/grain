package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Meta annotation for conditional annotations. Marks the annotation to be processed
 * on Grain initialization.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Conditional {
    Class<? extends AbstractConditionEvaluator<? extends Annotation>> value();
}
