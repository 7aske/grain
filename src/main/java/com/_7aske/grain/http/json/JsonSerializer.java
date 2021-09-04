package com._7aske.grain.http.json;

import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class JsonSerializer<T> {
	private final Class<T> clazz;

	public JsonSerializer(Class<T> clazz) {
		this.clazz = clazz;
	}

	public List<Object> serialize(Class<?> type, JsonArray arr) {
		List<Object> res = new ArrayList<>();
		for (Object object : arr) {
			if (Number.class.isAssignableFrom(type) ||
					String.class.isAssignableFrom(type) ||
					Boolean.class.isAssignableFrom(type)) {
				res.add(object);
			} else if (List.class.isAssignableFrom(type)) {
				res.add(serialize(type, (JsonArray) object));
			} else {
				if (object == null) {
					res.add(null);
				} else {
					JsonSerializer<?> serializer = new JsonSerializer<>(type);
					Object val = serializer.serialize((JsonObject) object);
					res.add(val);
				}
			}
		}

		return res;
	}

	public T serialize(JsonObject object) {
		try {
			Constructor<T> constructor = ReflectionUtil.getAnyConstructor(clazz);
			T instance = constructor.newInstance();
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(JsonIgnore.class)){
					continue;
				}
				field.setAccessible(true);
				Class<?> type = field.getType();
				String name = field.getName();
				if (Number.class.isAssignableFrom(type) ||
						String.class.isAssignableFrom(type) ||
						Boolean.class.isAssignableFrom(type)) {
					Object val = object.get(name);
					field.set(instance, val);
				} else if (List.class.isAssignableFrom(type)) {
					Class<?> genericType = (Class<T>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					List<Object> list = serialize(genericType, (JsonArray) object.get(name));
					field.set(instance, list);
				} else {
					if (object.get(name) == null) {
						field.set(instance, null);
					} else {
						JsonSerializer<?> serializer = new JsonSerializer<>(field.getType());
						Object val = serializer.serialize((JsonObject) object.get(name));
						field.set(instance, val);
					}
				}
				field.setAccessible(false);
			}
			return instance;

		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

	}
}
