package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.List;

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

	public List<Field> getModelIds() {
		return model.getIds();
	}

	public Model getModel() {
		return model;
	}
}
