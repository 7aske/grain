package com._7aske.grain.controller.converter;

import com._7aske.grain.component.AfterInit;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.controller.converter.impl.PageableConverter;
import com._7aske.grain.orm.page.Pageable;

/**
 * Here we register all the converters we might need.
 */
@Grain final class ConverterRegistryConfigurerRunner {
	@Inject
	private ConverterRegistry converterRegistry;

	@AfterInit
	public void setup() {
		converterRegistry.registerConverter(Pageable.class, new PageableConverter());
		converterRegistry.registerConverter(Integer.class, Integer::parseInt);
		converterRegistry.registerConverter(Float.class, Float::parseFloat);
		converterRegistry.registerConverter(Long.class, Long::parseLong);
		converterRegistry.registerConverter(Boolean.class, Boolean::parseBoolean);
		converterRegistry.registerConverter(Short.class, Short::parseShort);
		converterRegistry.registerConverter(Byte.class, Byte::parseByte);
	}
}
