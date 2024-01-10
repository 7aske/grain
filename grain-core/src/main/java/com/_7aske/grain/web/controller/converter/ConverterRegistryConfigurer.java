package com._7aske.grain.web.controller.converter;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.orm.page.Pageable;
import com._7aske.grain.web.controller.converter.impl.PageableConverter;

/**
 * Here we register all the converters we might need.
 */
@Grain
public class ConverterRegistryConfigurer {
	public ConverterRegistryConfigurer(ConverterRegistry converterRegistry) {
		setup(converterRegistry);
	}

	public void setup(ConverterRegistry registry) {
		registry.registerConverter(Pageable.class, new PageableConverter());
		registry.registerConverter(Integer.class, Integer::parseInt);
		registry.registerConverter(Float.class, Float::parseFloat);
		registry.registerConverter(Long.class, Long::parseLong);
		registry.registerConverter(Boolean.class, Boolean::parseBoolean);
		registry.registerConverter(Short.class, Short::parseShort);
		registry.registerConverter(Byte.class, Byte::parseByte);
		registry.registerConverter(Double.class, Double::parseDouble);
		registry.registerConverter(String.class, String::valueOf);
	}
}
