package com._7aske.grain.http.form;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE_PARAMETER})
public @interface FormBody {
}
