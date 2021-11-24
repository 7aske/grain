package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com._7aske.grain.util.ReflectionUtil.getAnyConstructor;

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
		data.forEach(d -> {
			try {
				T model = (T) getAnyConstructor(modelClazz).newInstance();

				for (Field field : model.getFields()) {
					Column column = field.getAnnotation(Column.class);
					String val = d.get(column.name());
					field.setAccessible(true);

					assignValueToField(model, field, val, d);
				}

				for (Field field : model.getManyToOne()) {
					ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
					String val = d.get(manyToOne.column().name());
					field.setAccessible(true);

					assignValueToField(model, field, val, d);
				}

				models.add(model);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});

		return models;
	}

	private void assignValueToField(Model model, Field field, String val, Map<String, String> currData) throws IllegalAccessException {
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
				try {
					Model instance = (Model) getAnyConstructor(field.getType()).newInstance();
					Table table = instance.getTable();
					for (Field f : instance.getFields()) {
						Column column = f.getAnnotation(Column.class);
						String v = currData.get(String.format("%s_%s", table.name(), column.name()));
						f.setAccessible(true);
						assignValueToField(instance, f, v, currData);
					}
					field.set(model, instance);
					// @Refactor
				} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			} else {
				// @Temporary
				System.err.printf("Setting value to type %s%n", field.getType());
				field.set(model, val);
			}
		}
	}
}
