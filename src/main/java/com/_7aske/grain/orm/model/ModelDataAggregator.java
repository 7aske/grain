package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.QueryBuilderUtil.*;
import static com._7aske.grain.util.ReflectionUtil.getGenericListTypeArgument;
import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

/**
 * Class used to aggregate raw data fetched from the database
 * in cases of join with one or more OneToMany columns.
 */
public class ModelDataAggregator<T extends Model> {
	public final Class<T> clazz;
	public final List<Map<String, String>> data;
	public final List<Field> ids;
	public final List<Field> oneToMany;
	public final List<Field> fields;

	public ModelDataAggregator(Class<T> clazz, List<Map<String, String>> data) {
		this.clazz = clazz;
		this.data = data;
		this.ids = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Id.class))
				.collect(Collectors.toList());

		this.oneToMany = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, OneToMany.class))
				.collect(Collectors.toList());

		this.fields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(ManyToOne.class))
				.collect(Collectors.toList());
	}

	public List<Map<String, Object>> aggregate() {
		return doAggregate(clazz, data);
	}

	public List<Map<String, Object>> doAggregate(Class<? extends Model> clazz, List<Map<String, String>> data) {
		Map<String, Map<String, Object>> result = new HashMap<>();
		for (Map<String, String> row : data) {
			String tableName = getTableName(clazz);
			String superIdColumnName = getIdColumnName(clazz);
			String superKey = String.format("%s.%s", tableName, superIdColumnName);
			Map<String, Object> forAggregate = new HashMap<>();
			for (Field field : getColumnFields(clazz)) {
				String fieldName = getColumnName(field);
				String key = String.format("%s.%s", tableName, fieldName);
				Object value = row.get(key);
				if (field.isAnnotationPresent(ManyToOne.class)) {
					value = doAggregate((Class<? extends Model>) field.getType(), Collections.singletonList(row)).get(0);
				}
				forAggregate.put(fieldName, value);
			}

			for (Field field : getListFields(clazz)) {
				String fieldName = field.getName();
				Class<? extends Model> listClazz = getGenericListTypeArgument(field);
				String idFieldName = getIdFieldColumnName(listClazz);
				String listTableName = getTableName(listClazz);
				String key = String.format("%s.%s", listTableName, idFieldName);
				List<Map<String, String>> relevantData = data.stream()
						.filter(d -> d.containsKey(key) && d.get(key) != null && d.get(superKey).equals(forAggregate.get(superIdColumnName)))
						.collect(Collectors.toList());
				forAggregate.put(fieldName, doAggregate(listClazz, relevantData));
			}
			result.put(row.get(superKey), forAggregate);
		}

		return new ArrayList<>(result.values());
	}
}
