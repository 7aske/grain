package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Marks a field or parameter for dependency injection. For parameters @Inject
 * annotation is implicit but can be used to denote the name or annotation based injection.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Inject {
	String name() default "";
	boolean required() default true;
	Class<? extends Annotation>[] annotatedBy() default {};
}
