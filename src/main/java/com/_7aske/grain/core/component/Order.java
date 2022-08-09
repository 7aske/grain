package com._7aske.grain.core.component;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Order {
	int DEFAULT = 0;

	int value() default DEFAULT;
}
