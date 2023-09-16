package com._7aske.grain.http.json;

import com._7aske.grain.http.json.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Class responsible for converting an object of class to its JSON
 * string representation.
 */
@Deprecated
public class JsonSerializer<T> {
	private final Class<T> clazz;

	public JsonSerializer(Class<T> clazz) {
		this.clazz = clazz;
	}

	private JsonArray doSerialize(List<Object> arr) {
		if (arr == null) {
			return null;
		}
		JsonArray res = new JsonArray();
		for (Object object : arr) {
			if (object == null) {
				res.add(null);
				continue;
			}
			Class<?> type = object.getClass();
			if (Number.class.isAssignableFrom(type) ||
					String.class.isAssignableFrom(type) ||
					Boolean.class.isAssignableFrom(type)) {
				res.add(object);
				// @Incomplete handle array type
			} else if (List.class.isAssignableFrom(type)) {
				res.add(doSerialize((List<Object>) object));
			} else {
				JsonSerializer<?> serializer = new JsonSerializer<>(type);
				Object val = serializer.doSerialize(object);
				res.add(val);
			}
		}

		return res;
	}

	public Object serialize(Object instance) {
		// @Incomplete handle array type
		if (Collection.class.isAssignableFrom(clazz)) {
			return doSerialize((List<Object>) instance);
		} else {
			return doSerialize(instance);
		}
	}

	private JsonObject doSerialize(Object instance) {
		try {
			JsonObject object = new JsonObject();

			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(JsonIgnore.class)) {
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
					// @Incomplete handle array types
				} else if (List.class.isAssignableFrom(fieldType)) {
					JsonArray list = doSerialize((List<Object>) field.get(instance));
					object.putArray(fieldName, list);
				} else {
					if (field.get(instance) == null) {
						object.putNull(fieldName);
					} else {
						JsonSerializer<?> serializer = new JsonSerializer<>(field.getType());
						JsonObject val = serializer.doSerialize(field.get(instance));
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
