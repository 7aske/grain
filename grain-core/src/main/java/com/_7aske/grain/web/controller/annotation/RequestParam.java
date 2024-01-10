package com._7aske.grain.web.controller.annotation;

import com._7aske.grain.constants.ValueConstants;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
	String value();

	boolean required() default true;

	String defaultValue() default ValueConstants.DEFAULT_NONE;
}
