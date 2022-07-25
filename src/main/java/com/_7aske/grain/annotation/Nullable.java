package com._7aske.grain.annotation;

import java.lang.annotation.*;

@Documented
@Retention(value = RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Nullable {
}
