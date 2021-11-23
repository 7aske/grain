package com._7aske.grain.http.form;

import com._7aske.grain.util.RequestParams;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com._7aske.grain.util.ReflectionUtil.getAnyConstructor;

public class FormDataMapper<T> {
	private final Class<T> clazz;

	public FormDataMapper(Class<T> modelClazz) {
		this.clazz = modelClazz;
	}

	// Maps all result entries to objects of model class type
	public T parse(Map<String, Object> data) {
			try {
				T instance = getAnyConstructor(clazz).newInstance();
				if (data == null)
					return instance;
				RequestParams dataExtractor = new RequestParams(data);
				for (Field field : clazz.getDeclaredFields()) {
					field.setAccessible(true);

					String val = dataExtractor.getStringParameter(field.getName());

					// @Refactor this is ugly
					if (val == null) {
						field.set(instance, null);
					} else if (Byte.class.isAssignableFrom(field.getType())) {
						field.set(instance, Byte.parseByte(val));
					} else if (Byte.class.isAssignableFrom(field.getType())) {
						field.set(instance, Short.parseShort(val));
					} else if (Integer.class.isAssignableFrom(field.getType())) {
						field.set(instance, Integer.parseInt(val));
					} else if (Float.class.isAssignableFrom(field.getType())) {
						field.set(instance, Float.parseFloat(val));
					} else if (Double.class.isAssignableFrom(field.getType())) {
						field.set(instance, Double.parseDouble(val));
					} else if (Boolean.class.isAssignableFrom(field.getType())) {
						field.set(instance, Boolean.parseBoolean(val));
					} else if (Long.class.isAssignableFrom(field.getType())) {
						field.set(instance, Long.parseLong(val));
					} else if (Character.class.isAssignableFrom(field.getType())) {
						// @Note @CopyPasta this is probably bad
						field.set(instance, val.charAt(0));
					} else if (String.class.isAssignableFrom(field.getType())) {
						field.set(instance, val);
					} else {
						// @Temporary this should be handeles better since we don't
						// really properly handle arrays which are by
						// the x-www-form-urlencoded spec.
						System.err.printf("Setting value to type %s%n", field.getType());
						field.set(instance, dataExtractor.getParameter(field.getName()));
					}
				}
				return instance;
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
	}
}
