package com._7aske.grain.component;


import java.lang.annotation.*;

@Grain
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Controller {

}
