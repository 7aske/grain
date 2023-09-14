package com._7aske.grain.util;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.orm.annotation.*;
import com._7aske.grain.orm.exception.GrainDbIntrospectionException;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.querybuilder.Join;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.getGenericListTypeArgument;
import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class QueryBuilderUtil {
	private static final Logger logger = LoggerFactory.getLogger(QueryBuilderUtil.class);

	private QueryBuilderUtil() {
	}

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
			fieldName = field.getAnnotation(ManyToOne.class).column();
		} else {
			// @Temporary
			throw new RuntimeException("No valid field for alias generation");
		}
		String hash = Integer.valueOf(Math.abs(clazz.hashCode())).toString().replace(",", "");
		return MessageFormat.format("{0}.{1} as {0}_{2}_{1}",
				clazz.getAnnotation(Table.class).name(),
				fieldName,
				hash);
	}

	public static String getAlias(Class<? extends Model> clazz) {
		String hash = Integer.valueOf(Math.abs(clazz.hashCode())).toString().replace(",", "");
		return String.format("%s_%s", getTableNameForDialect(clazz), hash);
	}

	public static String getAlias(String tableName) {
		String hash = Integer.valueOf(Math.abs(tableName.hashCode())).toString().replace(",", "");
		return String.format("%s_%s", tableName, hash);
	}

	public static String getTableNameForDialect(Class<? extends Model> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			Table sourceTable = clazz.getAnnotation(Table.class);
			return sourceTable.name() == null ? StringUtils.camelToSnake(clazz.getSimpleName()) : sourceTable.name();
		}
		throw new GrainDbIntrospectionException("Referenced class is not annotated with @Table annotation");
	}

	public static String getTableName(Class<? extends Model> clazz) {
		if (clazz.isAnnotationPresent(Table.class))
			return clazz.getAnnotation(Table.class).name();
		throw new GrainDbIntrospectionException("Referenced class is not annotated with @Table annotation");
	}

	public static String getColumnNameForDialect(Field field) {
		if (!field.isAnnotationPresent(Column.class))
			throw new GrainDbIntrospectionException("Referenced field is not annotated with @Column annotation");
		Column column = field.getAnnotation(Column.class);
		return column.name() == null ? StringUtils.camelToSnake(field.getName()) : column.name();
	}

	public static String getColumnName(Field field) {
		if (field.isAnnotationPresent(Column.class))
			return field.getAnnotation(Column.class).name();
		if (field.isAnnotationPresent(ManyToOne.class))
			return field.getAnnotation(ManyToOne.class).column();
		throw new GrainDbIntrospectionException("Referenced field is not annotated with @Column or @ManyToOne annotation");
	}


	public static String getIdFieldColumnName(Class<? extends Model> clazz) {
		Field field = getIdField(clazz);
		if (field.isAnnotationPresent(Column.class))
			return field.getAnnotation(Column.class).name();
		throw new GrainDbIntrospectionException("Referenced field is not annotated with @Column annotation");
	}

	public static Field getIdField(Class<? extends Model> clazz) {
		List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Id.class))
				.collect(Collectors.toList());

		// @Temporary
		if (fields.size() > 1)
			logger.warn("Called getId for class with a composite ID");

		return fields.get(0);
	}

	public static String getIdColumnName(Class<? extends Model> clazz) {
		Field field = getIdField(clazz);
		return getColumnNameForDialect(field);
	}

	public static List<Join<?, ?>> getJoins(ModelClass clazz, Stack<Join<?, ?>> stack) {
		Join<?, ?> last = stack.isEmpty() ? null : stack.pop();
		List<Join<?, ?>> result = new ArrayList<>();

		clazz.getManyToOne().forEach(f -> {
			Class<? extends Model> type = f.getType();
			ManyToOne anno = f.getAnnotation(ManyToOne.class);
			if (!anno.mappedBy().isEmpty()) {
				return;
			}
			Join<?, ?> join = Join.from(last == null ? clazz.getTableName() : last.alias(), anno.column(), anno.table(), anno.referencedColumn(), getTableFields(type));
			result.add(join);
			stack.add(join);
			result.addAll(getJoins(new ModelClass(type), stack));
		});

		clazz.getOneToMany().forEach(f -> {
			Class<? extends Model> type = f.getGenericListTypeArgument();
			OneToMany anno = f.getAnnotation(OneToMany.class);
			Join<?, ?> join = Join.from(last == null ? clazz.getTableName() : last.alias(), anno.column(), anno.table(), anno.referencedColumn(), getTableFields(type));
			result.add(join);
			stack.add(join);
			result.addAll(getJoins(new ModelClass(type), stack));
		});
		return result;
	}

	public static List<Field> getListFields(Class<? extends Model> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(OneToMany.class))
				.collect(Collectors.toList());
	}

	public static List<String> getTableFields(Class<? extends Model> clazz) {
		return getColumnFields(clazz)
				.stream()
				.map(f -> {
					if (f.isAnnotationPresent(Column.class)) {
						return f.getAnnotation(Column.class).name();
					} else {
						return f.getAnnotation(ManyToOne.class).column();
					}
				})
				.collect(Collectors.toList());
	}

	public static List<Field> getColumnFields(Class<? extends Model> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(ManyToOne.class))
				.collect(Collectors.toList());
	}
}
