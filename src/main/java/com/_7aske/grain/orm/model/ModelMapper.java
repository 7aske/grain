package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelMapper {
	private final Class<?> modelClazz;
	private final List<Map<String, Object>> data;

	public ModelMapper(Class<?> modelClazz, List<Map<String, Object>> data) {
		this.modelClazz = modelClazz;
		this.data = data;
	}

	// Maps all result entries to objects of model class type
	public List<Model> get() {
		List<Model> models = new ArrayList<>();
		// @Refactor use stream
		data.forEach(d -> {
			try {
				Model model = (Model) ReflectionUtil.getAnyConstructor(modelClazz).newInstance();

				for (Field field : model.getFields()) {
					Column column = field.getAnnotation(Column.class);
					field.setAccessible(true);
					field.set(model, d.get(column.name()));
				}

				models.add(model);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});

		return models;
	}
}
