package com._7aske.grain.web.http.codec.form;

import com._7aske.grain.web.exception.HttpException;
import com._7aske.grain.web.http.RequestParams;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static com._7aske.grain.core.reflect.ReflectionUtil.getAnyConstructor;

public class FormDataMapper<T> {
	private final Class<T> clazz;

	public FormDataMapper(Class<T> modelClazz) {
		this.clazz = modelClazz;
	}

	// Maps all result entries to objects of model class type
	public T parse(Map<String, String[]> data) {
			try {
				T instance = getAnyConstructor(clazz).newInstance();
				if (data == null)
					return instance;
				RequestParams dataExtractor = new RequestParams(data);
				for (Field field : clazz.getDeclaredFields()) {
					field.setAccessible(true);

					String val = String.join(",", dataExtractor.getArrayParameter(field.getName()));

					try {
						// @Refactor this is ugly
						if (val == null) {
							field.set(instance, null);
						} else if (Byte.class.isAssignableFrom(field.getType())) {
							field.set(instance, Byte.parseByte(val));
						} else if (Short.class.isAssignableFrom(field.getType())) {
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
						} else if (LocalDate.class.isAssignableFrom(field.getType())) {
							field.set(instance, LocalDate.parse(val));
						} else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
							field.set(instance, LocalDateTime.parse(val));
						} else if (String.class.isAssignableFrom(field.getType())) {
							field.set(instance, val);
						} else {
							// @Temporary this should be handled better since we don't
							// really properly handle arrays which are by
							// the x-www-form-urlencoded spec.
							System.err.printf("Setting value to type %s%n", field.getType());
							field.set(instance, dataExtractor.getParameter(field.getName()));
						}
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
					}
				}
				return instance;
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				throw new HttpException.BadRequest(e);
			}
	}
}
