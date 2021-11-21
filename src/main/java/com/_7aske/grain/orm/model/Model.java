package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model class representing a database entity/entry. Also contains all the required logic to generate
 * queries.
 */
public class Model {
	Table table;
	List<Field> fields;
	List<Field> ids;
	protected Model() {
		// @Incomplete: error handling if models do have id's set
		// @Incomplete: error handling if models do have table annotation set
		table = getClass().getAnnotation(Table.class);
		ids = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> ReflectionUtil.isAnnotationPresent(f, Id.class))
				.collect(Collectors.toList());
		fields = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> ReflectionUtil.isAnnotationPresent(f, Column.class))
				.collect(Collectors.toList());
	}

	Table getTable() {
		return table;
	}

	List<Field> getFields() {
		return fields;
	}

	List<Field> getIds() {
		return ids;
	}

	public static Model findById(Object id) {
		return null;
	}

	public static List<Model> findAll() {
		return null;
	}

	public Model save() {
		return null;
	}

	public Model update() {
		return null;
	}

	public void delete() {

	}
}
