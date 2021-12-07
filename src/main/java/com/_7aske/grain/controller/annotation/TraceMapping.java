package com._7aske.grain.controller.annotation;

import com._7aske.grain.http.HttpMethod;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@RequestMapping(method = HttpMethod.TRACE)
public @interface TraceMapping {
	String value() default "/";
}
