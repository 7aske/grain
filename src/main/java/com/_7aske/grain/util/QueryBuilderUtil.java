package com._7aske.grain.util;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.annotation.Table;

import java.lang.reflect.Field;
import java.text.MessageFormat;

public class QueryBuilderUtil {
	private QueryBuilderUtil() {}

	// Creates a formatted alias for usage in join queries
	public static String getFormattedAlias(Field field) {
		Class<?> clazz = field.getType();
		return getFormattedAlias(clazz, field);
	}

	public static String resolveAliasToFieldName(Class<?> clazz, String alias) {
		String hash = Integer.valueOf(clazz.hashCode()).toString().replace(",", "");
		String tableName = clazz.getAnnotation(Table.class).name();
		return alias.replace(String.format("%s_%s_", tableName, hash), "");
	}

	// Hashing should produce 'unique' alias to avoid collisions.
	public static String getFormattedAlias(Class<?> clazz, Field field) {
		String fieldName = null;
		if (field.isAnnotationPresent(Column.class)) {
			fieldName = field.getAnnotation(Column.class).name();
		} else if (field.isAnnotationPresent(OneToMany.class)) {
			fieldName = field.getAnnotation(OneToMany.class).referencedColumn();
		} else if (field.isAnnotationPresent(ManyToOne.class)) {
			fieldName = field.getAnnotation(ManyToOne.class).column().name();
		} else {
			// @Temporary
			throw new RuntimeException("No valid field for alias generation");
		}
		String hash = Integer.valueOf(clazz.hashCode()).toString().replace(",", "");
		return MessageFormat.format("{0}.{1} as {0}_{2}_{1}",
				clazz.getAnnotation(Table.class).name(),
				fieldName,
				hash);
	}
}
