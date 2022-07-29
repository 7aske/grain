package com._7aske.grain.util.properties;

import java.util.Map;
import java.util.function.Function;

public final class PropertiesHelper {
	private PropertiesHelper() {
	}

	public static <T> T getProperty(String key, Map<?, ?> map, Class<T> clazz, Function<String, T> converter, T defaultValue) {
		Object retval = map.get(key);

		if (clazz.isInstance(retval)) {
			return clazz.cast(retval);
		}

		if (retval instanceof String) {
			return converter.apply((String) retval);
		}

		return defaultValue;
	}

	public static <T> T getProperty(String key, Map<?, ?> map, Class<T> clazz, Function<String, T> converter) {
		return getProperty(key, map, clazz, converter, null);
	}
}
