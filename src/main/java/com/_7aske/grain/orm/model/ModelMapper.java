package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.exception.GrainDbIntrospectionException;
import com._7aske.grain.orm.querybuilder.helper.ManyToOneField;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;
import com._7aske.grain.orm.querybuilder.helper.ModelField;
import com._7aske.grain.orm.querybuilder.helper.OneToManyField;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com._7aske.grain.orm.querybuilder.AbstractQueryBuilder.*;
import static com._7aske.grain.util.ReflectionUtil.newInstance;

public class ModelMapper<T extends Model> {
	private final ModelClass modelClazz;
	private final List<Map<String, Object>> data;

	public ModelMapper(Class<T> modelClazz, List<Map<String, Object>> data) {
		this.modelClazz = new ModelClass(modelClazz);
		this.data = data;
	}

	// Maps all result entries to objects of model class type
	public List<T> get() {
		List<T> models = new ArrayList<>();
		// @Refactor use stream
		data.forEach(modelData -> {
			try {
				T model = (T) modelClazz.newInstance();

				for (ModelField field : modelClazz.getColumnFields()) {
					String val = (String) modelData.get(field.getColumnName());
					assignValueToField(model, field.getField(), val, modelData);
				}

				for (ManyToOneField field : modelClazz.getManyToOne()) {
					// @Temporary
					if (!field.getAnnotation(ManyToOne.class).mappedBy().isEmpty())
						continue;
					setFieldModelValue(model, field.getField(), (Map<String, Object>) modelData.get(field.getColumnName()));
				}

				for (OneToManyField field : modelClazz.getOneToMany()) {
					// @Temporary
					if (!field.getAnnotation(OneToMany.class).mappedBy().isEmpty())
						continue;
					List<Map<String, Object>> values = (List<Map<String, Object>>) modelData.get(field.getField().getName());
					Class<? extends Model> clazz = field.getGenericListTypeArgument();
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
		} else if (LocalDate.class.isAssignableFrom(field.getType())) {
			// @Refactor ugly
			try {
				field.set(model, LocalDate.parse(val, DATE_FORMAT));
			} catch (DateTimeParseException ex) {
				field.set(model, LocalDate.parse(val, DATE_FORMAT2));
			}
		} else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
			try {
				field.set(model, LocalDateTime.parse(val, DATE_TIME_FORMAT));
			} catch (DateTimeParseException ex) {
				field.set(model, LocalDateTime.parse(val, DATE_TIME_FORMAT2));
			}
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
		ModelClass newModelClass = new ModelClass((Class<? extends Model>) field.getType());
		for (ModelField f : newModelClass.getColumnAndManyToOneFields()) {
			if (f.isAnnotationPresent(Column.class)) {
				assignValueToField(instance, f.getField(), (String) modelData.get(f.getColumnName()), modelData);
			} else if (f.isAnnotationPresent(ManyToOne.class)) {
				setFieldModelValue(instance, f.getField(), (Map<String, Object>) modelData.get(f.getField().getName()));
			} else {
				throw new GrainDbIntrospectionException("Unsupported operation");
			}
		}
		field.set(model, instance);
	}
}
