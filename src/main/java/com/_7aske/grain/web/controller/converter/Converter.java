package com._7aske.grain.web.controller.converter;

/**
 * Describes how request parameter strings are converted to objects.
 * @param <T> Type of the object that the parameters is converted to.
 */
public interface Converter<T> {
	T convert(String param);
}
