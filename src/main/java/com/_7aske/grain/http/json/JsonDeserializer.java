package com._7aske.grain.http.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public class JsonDeserializer<T> {
	private final Class<T> clazz;

	public JsonDeserializer(Class<T> clazz) {
		this.clazz = clazz;
	}

	private JsonArray doDeserialize(Class<?> type, List<Object> arr) {
		JsonArray res = new JsonArray();
		for (Object object : arr) {
			if (Number.class.isAssignableFrom(type) ||
					String.class.isAssignableFrom(type) ||
					Boolean.class.isAssignableFrom(type)) {
				res.add(object);
			} else if (List.class.isAssignableFrom(type)) {
				res.add(doDeserialize(type, (List<Object>) object));
			} else {
				if (object == null) {
					res.add(null);
				} else {
					JsonDeserializer<?> deserializer = new JsonDeserializer<>(type);
					Object val = deserializer.doDeserialize(object);
					res.add(val);
				}
			}
		}

		return res;
	}

	public Object deserialize(Object instance) {
		if (Collection.class.isAssignableFrom(clazz)) {
			return doDeserialize(clazz, (List<Object>) instance);
		} else {
			return doDeserialize(instance);
		}
	}

	private JsonObject doDeserialize(Object instance) {
		try {
			JsonObject object = new JsonObject();

			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(JsonIgnore.class)){
					continue;
				}
				field.setAccessible(true);
				Class<?> fieldType = field.getType();
				String fieldName = field.getName();
				if (Number.class.isAssignableFrom(fieldType)) {
					object.putNumber(fieldName, (Number) field.get(instance));
				} else if (String.class.isAssignableFrom(fieldType)) {
					object.putString(fieldName, (String) field.get(instance));
				} else if (Boolean.class.isAssignableFrom(fieldType)) {
					object.putBoolean(fieldName, (Boolean) field.get(instance));
				} else if (List.class.isAssignableFrom(fieldType)) {
					Class<?> genericType = (Class<T>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					JsonArray list = doDeserialize(genericType, (List<Object>) field.get(instance));
					object.putArray(fieldName, list);
				} else {
					if (field.get(instance) == null) {
						object.putNull(fieldName);
					} else {
						JsonDeserializer<?> deserializer = new JsonDeserializer<>(field.getType());
						JsonObject val = deserializer.doDeserialize(field.get(instance));
						object.putObject(fieldName, val);
					}
				}
				field.setAccessible(false);
			}
			return object;

		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

	}
}
