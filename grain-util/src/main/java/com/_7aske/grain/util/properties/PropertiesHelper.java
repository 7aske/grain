package com._7aske.grain.util.properties;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class providing methods that allow type safe extracting of values
 * from {@link Map}s.
 */
public final class PropertiesHelper {
	private PropertiesHelper() {
	}

	/**
	 * Retrieves a property from a {@link Map} using the given key.
	 *
	 * @param key          the key to use for retrieving the property
	 * @param map          the map to retrieve the property from
	 * @param clazz        the class of the property to retrieve
	 * @param converter    the converter to use for converting the property to the
	 *                     desired type. Converter if the property is of type String.
	 * @param defaultValue the default value to return if the property is not
	 *                     found in the map.
	 * @param <T>          the type of the property to retrieve and convert to.
	 * @return the property value, or the default value if the property is not
	 * found in the map.
	 */
	public static @Nullable <T> T getProperty(@NotNull String key,
	                                @NotNull Map<?, ?> map,
	                                @NotNull Class<T> clazz,
	                                @NotNull Function<String, T> converter,
	                                @Nullable T defaultValue) {
		Object retval = map.get(key);

		if (clazz.isInstance(retval)) {
			return clazz.cast(retval);
		}

		if (retval instanceof String str) {
			return converter.apply(str);
		}

		return defaultValue;
	}

	/**
	 * Overload of {@link #getProperty(String, Map, Class, Function, T)} that returns
	 * null as a default value.
	 *
	 * @param key          the key to use for retrieving the property
	 * @param map          the map to retrieve the property from
	 * @param clazz        the class of the property to retrieve
	 * @param converter    the converter to use for converting the property to the
	 *                     desired type. Converter if the property is of type String.
	 * @param <T>          the type of the property to retrieve and convert to.
	 * @return the property value, or the default value if the property is not
	 * found in the map.
	 */
	public static @Nullable <T> T getProperty(@NotNull String key,
	                                          @NotNull Map<?, ?> map,
	                                          @NotNull Class<T> clazz,
	                                          @NotNull Function<String, T> converter) {
		return getProperty(key, map, clazz, converter, null);
	}

	/**
	 * Retrieves a property from a {@link Map} using the given key.
	 *
	 * @param key          the key to use for retrieving the property
	 * @param map          the map to retrieve the property from
	 * @param clazz        the class of the property to retrieve
	 * @param converter    the converter to use for converting the property to the
	 *                     desired type. Converter if the property is of type String.
	 * @param <T>          the type of the property to retrieve and convert to.
	 * @return the property value, or the default value if the property is not
	 * found in the map.
	 */
	public static @NotNull <T> Optional<T> getOptionalProperty(@NotNull String key,
	                                                           @NotNull Map<?, ?> map,
	                                                           @NotNull Class<T> clazz,
	                                                           @NotNull Function<String, T> converter) {
		return Optional.ofNullable(getProperty(key, map, clazz, converter, null));
	}

	/**
	 * Retrieves a property from a {@link Map} using the given key. Throws if
	 * the property is not found in the map.
	 *
	 * @param key          the key to use for retrieving the property
	 * @param map          the map to retrieve the property from
	 * @param clazz        the class of the property to retrieve
	 * @param converter    the converter to use for converting the property to the
	 *                     desired type. Converter if the property is of type String.
	 * @param <T>          the type of the property to retrieve and convert to.
	 * @return the property value.
	 */
	public static @NotNull <T> T getRequiredProperty(@NotNull String key,
	                                                 @NotNull Map<?, ?> map,
	                                                 @NotNull Class<T> clazz,
	                                                 @NotNull Function<String, T> converter) {
		return getOptionalProperty(key, map, clazz, converter)
				.orElseThrow(() -> new NoSuchElementException("Required property " + key + " not found"));
	}
}
