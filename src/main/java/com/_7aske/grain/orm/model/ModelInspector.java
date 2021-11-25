package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used for exposing model getters to QueryBuilder classes to
 * allow proper query string creation.
 */
public final class ModelInspector {
	private final Model model;

	public ModelInspector(Model model) {
		this.model = model;
	}

	public Table getModelTable() {
		return model.getTable();
	}

	public List<Field> getModelFields() {
		return model.getFields();
	}

	public List<Field> getAllModelFields() {
		return Arrays.stream(model.getClass().getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(ManyToOne.class))
				.collect(Collectors.toList());
	}

	public List<Field> getModelIds() {
		return model.getIds();
	}

	public Model getModel() {
		return model;
	}

	public List<Field> getModelManyToOne() {
		return model.getManyToOne();
	}

	public List<Field> getModelOneToMany() {
		return model.getOneToMany();
	}
}
