package com._7aske.grain.web.controller.annotation;

import com._7aske.grain.http.HttpMethod;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {
	String value() default "/";

	HttpMethod method() default HttpMethod.GET;
}
