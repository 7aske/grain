package com._7aske.grain.core.cache.annotation;

import com._7aske.grain.core.cache.annotation.meta.CacheAware;

import java.lang.annotation.*;

@CacheAware
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {
    String value() default "";

    boolean allEntries() default false;

    String key() default "";

    String condition() default "true";
}
