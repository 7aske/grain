package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Marks the annotated method to be executed after Grain initialization.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterInit {
}
