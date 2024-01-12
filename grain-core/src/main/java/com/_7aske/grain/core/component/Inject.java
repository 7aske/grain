package com._7aske.grain.core.component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Inject {
	String name() default "";
	boolean required() default true;
	Class<? extends Annotation>[] annotatedBy() default {};
}
