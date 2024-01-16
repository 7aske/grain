package com._7aske.grain.web.controller.annotation;


import com._7aske.grain.core.component.Grain;

import java.lang.annotation.*;

@Grain
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExceptionController {

}
