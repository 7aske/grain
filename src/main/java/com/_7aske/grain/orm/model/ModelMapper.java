package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.exception.GrainDbIntrospectionException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com._7aske.grain.util.QueryBuilderUtil.getColumnName;
import static com._7aske.grain.util.ReflectionUtil.getGenericListTypeArgument;
import static com._7aske.grain.util.ReflectionUtil.newInstance;

public class ModelMapper<T extends Model> {
	private final Class<?> modelClazz;
	private final List<Map<String, Object>> data;

	public ModelMapper(Class<T> modelClazz, List<Map<String, Object>> data) {
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
					String val = (String) modelData.get(getColumnName(field));
					assignValueToField(model, field, val, modelData);
				}

				for (Field field : model.getManyToOne()) {
					setFieldModelValue(model, field, (Map<String, Object>) modelData.get(getColumnName(field)));
				}

				for (Field field : model.getOneToMany()) {
					List<Map<String, Object>> values = (List<Map<String, Object>>) modelData.get(field.getName());
					field.setAccessible(true);
					Class<? extends Model> clazz = getGenericListTypeArgument(field);
					List<?> toSet = new ModelMapper<>(clazz, values).get();
					field.set(model, toSet);
				}

				models.add(model);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});

		return models;
	}

	private void assignValueToField(Model model, Field field, String val, Map<String, Object> modelData) throws IllegalAccessException {
		field.setAccessible(true);
		// @Refactor
		// This is nasty hack to handle different number types that
		// might be set to the model class.
		if (val == null) {
			field.set(model, null);
		} else if (Byte.class.isAssignableFrom(field.getType())) {
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
			} else {
				// @Temporary until we handle all the cases
				System.err.printf("Setting value to type %s%n", field.getType());
				field.set(model, val);
			}
		}
	}

	private void setFieldModelValue(Model model, Field field, Map<String, Object> modelData) throws IllegalAccessException {
		field.setAccessible(true);
		Model instance = (Model) newInstance(field.getType());
		for (Field f : instance.getFields()) {
			f.setAccessible(true);
			if (f.isAnnotationPresent(Column.class)) {
				assignValueToField(instance, f, (String) modelData.get(getColumnName(f)), modelData);
			} else if (f.isAnnotationPresent(ManyToOne.class)) {
				setFieldModelValue(instance, f, (Map<String, Object>) modelData.get(f.getName()));
			} else {
				throw new GrainDbIntrospectionException("Unsupported operation");
			}
		}
		field.set(model, instance);
	}
}
