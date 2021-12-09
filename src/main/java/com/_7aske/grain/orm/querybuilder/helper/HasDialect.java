package com._7aske.grain.orm.querybuilder.helper;

import com._7aske.grain.util.StringUtils;

import java.util.function.Function;

public interface HasDialect {
	default String applyDialect(String str) {
		final Function<String, String> dialect = StringUtils::camelToSnake;
		return dialect.apply(str);
	}
}
