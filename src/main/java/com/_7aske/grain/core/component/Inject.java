package com._7aske.grain.core.component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject {
	String name() default "";
	boolean required() default true;
}
