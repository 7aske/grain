package com._7aske.grain.component;

import java.lang.annotation.*;

/**
 * Used to allow conditional loading of dependencies in dependency injection
 * system. Value of the annotation will be evaluated with an interpreter
 * populated with data from the Configuration object. If resulting value
 * can be converted to a true Boolean class will be loaded.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Condition {
	String value() default "true";
}
