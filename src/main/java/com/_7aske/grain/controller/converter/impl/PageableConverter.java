package com._7aske.grain.controller.converter.impl;

import com._7aske.grain.controller.converter.Converter;
import com._7aske.grain.orm.page.DefaultPageable;
import com._7aske.grain.orm.page.Pageable;

public class PageableConverter implements Converter<Pageable> {
	public static final int DEFAULT_PAGE_SIZE = 10;

	// @Refactor should query string be guaranteed not null?
	@Override
	public Pageable convert(String queryString) {
		if (queryString == null || queryString.isEmpty())
			return null;

		String[] attrs = queryString.split(",");

		return new DefaultPageable(
				parsePageNumber(attrs),
				parsePageSize(attrs)
		);
	}

	private Integer parsePageNumber(String[] attrs) {
		if (attrs.length == 0)
			return 0;
		return Integer.parseInt(attrs[0]);
	}

	private Integer parsePageSize(String[] attrs) {
		if (attrs.length <= 1)
			return DEFAULT_PAGE_SIZE;

		try {
			return Integer.parseInt(attrs[1]);
		} catch (NumberFormatException ex) {
			return DEFAULT_PAGE_SIZE;
		}
	}
}
