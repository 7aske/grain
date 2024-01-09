package com._7aske.grain.web.controller.annotation;

import com._7aske.grain.web.http.HttpStatus;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface ResponseStatus {
    int NO_VALUE = -1;

    HttpStatus value() default HttpStatus.OK;

    int code() default NO_VALUE;
}
