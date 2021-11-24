package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com._7aske.grain.util.ReflectionUtil.newInstance;

public class ModelMapper<T extends Model> {
	private final Class<?> modelClazz;
	private final List<Map<String, String>> data;

	public ModelMapper(Class<T> modelClazz, List<Map<String, String>> data) {
		this.modelClazz = modelClazz;
		this.data = data;
	}

	// Maps all result entries to objects of model class type
	public List<T> get() {
		List<T> models = new ArrayList<>();
		// @Refactor use stream
		data.forEach(modelData -> {
			try {
				T model = (T) newInstance(modelClazz);

				for (Field field : model.getFields()) {
					Column column = field.getAnnotation(Column.class);
					String val = modelData.get(column.name());
					field.setAccessible(true);

					assignValueToField(model, field, val, modelData);
				}

				for (Field field : model.getManyToOne()) {
					ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
					String val = modelData.get(manyToOne.column().name());
					field.setAccessible(true);

					assignValueToField(model, field, val, modelData);
				}

				models.add(model);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});

		return models;
	}

	private void assignValueToField(Model model, Field field, String val, Map<String, String> modelData) throws IllegalAccessException {
		// @Refactor
		// This is nasty hack to handle different number types that
		// might be set to the model class.
		if (Byte.class.isAssignableFrom(field.getType())) {
			field.set(model, Byte.parseByte(val));
		} else if (Byte.class.isAssignableFrom(field.getType())) {
			field.set(model, Short.parseShort(val));
		} else if (Integer.class.isAssignableFrom(field.getType())) {
			field.set(model, Integer.parseInt(val));
		} else if (Float.class.isAssignableFrom(field.getType())) {
			field.set(model, Float.parseFloat(val));
		} else if (Double.class.isAssignableFrom(field.getType())) {
			field.set(model, Double.parseDouble(val));
		} else if (Boolean.class.isAssignableFrom(field.getType())) {
			field.set(model, Boolean.parseBoolean(val));
		} else if (Long.class.isAssignableFrom(field.getType())) {
			field.set(model, Long.parseLong(val));
		} else if (Character.class.isAssignableFrom(field.getType())) {
			// @Note this is probably bad
			field.set(model, val.charAt(0));
		} else if (String.class.isAssignableFrom(field.getType())) {
			field.set(model, val);
		} else {
			if (field.isAnnotationPresent(ManyToOne.class)) {
				setFieldModelValue(model, field, modelData);
				System.err.println(Thread.currentThread().getStackTrace()[0]);
			} else {
				// @Temporary until we handle all the cases
				System.err.println(Thread.currentThread().getStackTrace()[0]);
				System.err.printf("Setting value to type %s%n", field.getType());
				field.set(model, val);
			}
		}
	}

	private void setFieldModelValue(Model model, Field field, Map<String, String> modelData) throws IllegalAccessException {
		Model instance = (Model) newInstance(field.getType());
		Table table = instance.getTable();
		for (Field f : instance.getFields()) {
			f.setAccessible(true);
			Column column = f.getAnnotation(Column.class);
			// @Refactor alias should be generated in a common place
			// across all files that use it.
			String v = modelData.get(String.format("%s_%s", table.name(), column.name()));
			assignValueToField(instance, f, v, modelData);
		}
		field.set(model, instance);
	}
}
