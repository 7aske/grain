package com._7aske.grain.orm.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToMany {
	String joinTable();
	Column joinColumn();
	Column inverseJoinColumn();
}
