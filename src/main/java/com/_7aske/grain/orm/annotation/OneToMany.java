package com._7aske.grain.orm.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
	String column();
	String referencedColumn();
	String table();
}
