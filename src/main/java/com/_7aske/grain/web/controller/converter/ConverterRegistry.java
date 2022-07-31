package com._7aske.grain.web.controller.converter;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.exception.NoValidConverterException;

import java.util.HashMap;
import java.util.Map;

// @Refactor @Optimization maybe we convert converters to a list of key value pairs and
// then query it by matching clazz.isAssignableFrom(converterClass) and then
// save matching results to a cache so that we dont have to test every value
// from converters map every time
@Grain
public final class ConverterRegistry {
	public final Map<Class<?>, Converter<?>> converters;

	public ConverterRegistry() {
		this.converters = new HashMap<>();
	}

	public <T> void registerConverter(Class<T> clazz, Converter<T> converter) {
		converters.put(clazz, converter);
	}

	public <T> Converter<T>  getConverter(Class<T> clazz) {
		Converter<T> converter = (Converter<T>) converters.get(clazz);
		if (converter == null)
			throw new NoValidConverterException(clazz);
		return converter;
	}

	public boolean hasConverter(Class<?> clazz) {
		return this.converters.containsKey(clazz);
	}
}
