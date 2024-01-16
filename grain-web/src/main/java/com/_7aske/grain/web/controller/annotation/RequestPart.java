package com._7aske.grain.web.controller.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestPart {
    String value();

    boolean required() default true;
}
