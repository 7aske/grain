package com._7aske.grain.component;

import com._7aske.grain.http.HttpMethod;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Grain {
}
