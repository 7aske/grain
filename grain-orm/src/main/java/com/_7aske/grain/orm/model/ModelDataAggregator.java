package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.querybuilder.helper.ModelField;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;
import com._7aske.grain.orm.querybuilder.helper.OneToManyField;

import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.orm.querybuilder.QueryBuilderUtil.*;
import static com._7aske.grain.core.reflect.ReflectionUtil.isAnnotationPresent;

/**
 * Class used to aggregate raw data fetched from the database
 * in cases of join with one or more OneToMany columns.
 */
public class ModelDataAggregator<T extends Model> {
	public final ModelClass clazz;
	public final List<Map<String, String>> data;

	public ModelDataAggregator(Class<T> clazz, List<Map<String, String>> data) {
		this.clazz = new ModelClass(clazz);
		this.data = data;
	}

	public List<Map<String, Object>> aggregate() {
		return doAggregate(clazz, data);
	}

	public List<Map<String, Object>> doAggregate(ModelClass clazz, List<Map<String, String>> data) {
		Map<String, Map<String, Object>> result = new HashMap<>();
		for (Map<String, String> row : data) {
			String tableName = clazz.getTableName();
			String superIdColumnName = clazz.getIdColumnField().getColumnName();

			String superKey = String.format("%s.%s", tableName, superIdColumnName);
			Map<String, Object> forAggregate = new HashMap<>();

			for (ModelField field : clazz.getColumnAndManyToOneFields()) {
				String fieldName = field.getColumnName();
				String key = String.format("%s.%s", tableName, fieldName);
				Object value = row.get(key);
				// If the current entity is the owner of the relationship only
				// then we continue parsing.
				if (field.isAnnotationPresent(ManyToOne.class)){
					// @Temporary
					if (!field.getAnnotation(ManyToOne.class).mappedBy().isEmpty()) {
						continue;
					}
					value = doAggregate(new ModelClass(field.getType()), Collections.singletonList(row)).get(0);
				}
				forAggregate.put(fieldName, value);
			}

			for (OneToManyField field : clazz.getOneToMany()) {
				String fieldName = field.getField().getName();
				Class<? extends Model> listClazz = field.getGenericListTypeArgument();
				String idFieldName = getIdFieldColumnName(listClazz);
				String listTableName = getTableName(listClazz);
				String key = String.format("%s.%s", listTableName, idFieldName);
				// @Temporary
				// If the current entity is the owner of the relationship only
				// then we continue parsing.
				if (field.getAnnotation(OneToMany.class).mappedBy().isEmpty()) {
					List<Map<String, String>> relevantData = data.stream()
							.filter(d -> d.containsKey(key) && d.get(key) != null && d.get(superKey).equals(forAggregate.get(superIdColumnName)))
							.collect(Collectors.toList());
					forAggregate.put(fieldName, doAggregate(new ModelClass(listClazz), relevantData));
				}
			}

			result.put(row.get(superKey), forAggregate);
		}

		return new ArrayList<>(result.values());
	}
}
