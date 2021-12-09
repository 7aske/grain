package com._7aske.grain.orm.querybuilder.helper;

import java.lang.reflect.Field;

public class ManyToOneField extends ModelField implements HasDialect {
	public ManyToOneField(Field field) {
		super(field);
	}
}
