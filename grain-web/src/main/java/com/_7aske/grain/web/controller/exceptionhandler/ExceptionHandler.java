package com._7aske.grain.web.controller.exceptionhandler;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExceptionHandler {
    Class<? extends Throwable>[] value() default {};
}
