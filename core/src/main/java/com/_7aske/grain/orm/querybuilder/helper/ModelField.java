package com._7aske.grain.orm.querybuilder.helper;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.exception.GrainDbIntrospectionException;
import com._7aske.grain.orm.model.Model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ModelField implements HasDialect {
	private final Field field;

	public ModelField(Field field) {
		this.field = field;
		this.field.setAccessible(true);
	}

	public Object get(Object object) {
		try {
			return this.field.get(object);
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	public void set(Object object, Object value) {
		try {
			this.field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public String getColumnName() {
		if (this.field.isAnnotationPresent(ManyToOne.class)) {
			ManyToOne manyToOne = this.field.getAnnotation(ManyToOne.class);
			return manyToOne.column() == null || manyToOne.column().isEmpty() ? applyDialect(field.getName()) : manyToOne.column();
		}

		if (!this.field.isAnnotationPresent(Column.class))
			throw new GrainDbIntrospectionException("Referenced field is not annotated with @Column annotation");

		Column column = this.field.getAnnotation(Column.class);
		return column.name() == null || column.name().isEmpty() ? field.getName() : column.name();
	}

	public Field getField() {
		return field;
	}


	public Class<? extends Model> getType() {
		return (Class<? extends Model>) this.getField().getType();
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotation){
		return this.getField().getAnnotation(annotation);
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotation){
		return this.getField().isAnnotationPresent(annotation);
	}
}
