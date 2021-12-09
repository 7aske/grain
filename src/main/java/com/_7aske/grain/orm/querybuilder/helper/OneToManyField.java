package com._7aske.grain.orm.querybuilder.helper;

import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;

public class OneToManyField extends ModelField implements HasDialect {
	public OneToManyField(Field field) {
		super(field);
	}

	public <T> Class<T> getGenericListTypeArgument() {
		return ReflectionUtil.getGenericListTypeArgument(this.getField());
	}
}
