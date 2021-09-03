package com._7aske.grain.component;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Priority {
	int value() default Integer.MAX_VALUE;
}
