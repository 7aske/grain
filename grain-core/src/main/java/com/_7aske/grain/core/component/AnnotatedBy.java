package com._7aske.grain.core.component;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotatedBy {
    Class<? extends Annotation> value();
}
