package com._7aske.grain.orm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
	Column column();
	String referencedColumn();
	String table();
}
