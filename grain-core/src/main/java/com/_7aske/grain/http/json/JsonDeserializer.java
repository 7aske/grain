package com._7aske.grain.http.json;

import com._7aske.grain.http.json.annotation.JsonIgnore;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for conv JSON
 * string representation.
 */
public class JsonDeserializer<T> {
	private final Class<T> clazz;

	public JsonDeserializer(Class<T> clazz) {
		this.clazz = clazz;
	}

	public List<Object> deserialize(Class<?> type, JsonArray arr) {
		List<Object> res = new ArrayList<>();
		for (Object object : arr) {
			if (Number.class.isAssignableFrom(type) ||
					String.class.isAssignableFrom(type) ||
					Boolean.class.isAssignableFrom(type)) {
				res.add(object);
			} else if (List.class.isAssignableFrom(type)) {
				res.add(deserialize(type, (JsonArray) object));
			} else {
				if (object == null) {
					res.add(null);
				} else {
					JsonDeserializer<?> deserializer = new JsonDeserializer<>(type);
					Object val = deserializer.deserialize((JsonObject) object);
					res.add(val);
				}
			}
		}

		return res;
	}

	public T deserialize(JsonObject object) {
		try {
			Constructor<T> constructor = ReflectionUtil.getAnyConstructor(clazz);
			T instance = constructor.newInstance();
			for (Field field : instance.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(JsonIgnore.class)) {
					continue;
				}
				field.setAccessible(true);
				Class<?> type = field.getType();
				String name = field.getName();
				Object val = object.get(name);
				if (type.isPrimitive() ||
						Number.class.isAssignableFrom(type) ||
						String.class.isAssignableFrom(type) ||
						Boolean.class.isAssignableFrom(type)) {

					// @Refactor
					// This is nasty hack to handle different number types that
					// might be set to the model class.
					if (Byte.class.isAssignableFrom(field.getType())) {
						field.set(instance, Byte.parseByte(val.toString()));
					} else if (Byte.class.isAssignableFrom(field.getType())) {
						field.set(instance, Short.parseShort(val.toString()));
					} else if (Integer.class.isAssignableFrom(field.getType())) {
						field.set(instance, Integer.parseInt(val.toString()));
					} else if (Float.class.isAssignableFrom(field.getType())) {
						field.set(instance, Float.parseFloat(val.toString()));
					} else if (Double.class.isAssignableFrom(field.getType())) {
						field.set(instance, Double.parseDouble(val.toString()));
					} else if (Boolean.class.isAssignableFrom(field.getType())) {
						field.set(instance, Boolean.parseBoolean(val.toString()));
					} else if (Long.class.isAssignableFrom(field.getType())) {
						field.set(instance, Long.parseLong(val.toString()));
					} else if (Character.class.isAssignableFrom(field.getType())) {
						// @Note this is probably bad
						field.set(instance, val.toString().charAt(0));
					} else if (String.class.isAssignableFrom(field.getType())) {
						field.set(instance, val);
					} else {
						// @Temporary
						System.err.printf("Setting value to type %s%n", field.getType());
						field.set(instance, val);
					}
				} else if (List.class.isAssignableFrom(type)) {
					Class<?> genericType = (Class<T>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					List<Object> list = deserialize(genericType, (JsonArray) val);
					field.set(instance, list);
				} else {
					if (val == null) {
						field.set(instance, null);
					} else {
						JsonDeserializer<?> deserializer = new JsonDeserializer<>(field.getType());
						field.set(instance, deserializer.deserialize((JsonObject) val));
					}
				}
			}
			return instance;

		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
