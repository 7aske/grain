package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.QueryBuilderUtil.getFormattedAlias;
import static com._7aske.grain.util.QueryBuilderUtil.resolveAliasToFieldName;
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

	public ModelDataAggregator(Class<T> clazz, List<Map<String, String>> data) {
		this.clazz = clazz;
		this.data = data;
		this.ids = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Id.class))
				.collect(Collectors.toList());
		this.oneToMany = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, OneToMany.class))
				.collect(Collectors.toList());
	}

	private String getId() {
		// @Incomplete
		if (ids.size() > 1)
			System.err.println("Composite keys not yet supported");

		return ids.get(0).getAnnotation(Column.class).name();
	}

	public List<Map<String, Object>> aggregate() {
		Map<String, Map<String, Object>> aggregated = new HashMap<>();
		for (Map<String, String> d : data) {
			String id = d.get(getId());
			if (!aggregated.containsKey(id)) {
				// We iterate over OneToMany fields to create empty lists
				// that are going to be used for aggregation of said columns.
				Map<String, Object> forAggregate = new HashMap<>(d);
				for (Field field : this.oneToMany) {
					// We are certain that this is a container class(List etc.)
					// and therefore we're getting the generic parameter that
					// is supposed to be a model class so we can treat this
					// as @Incomplete and handle the error if the found type
					// is not derived from Model.
					Class<?> listClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					forAggregate.put(field.getName(), new ArrayList<>());

					// We're iterating over all declared fields of the model class
					// because there is no need to save the ID and then query the
					// database again for the actual object because we already
					// have all the data here.
					Map<String, String> fkObject = new HashMap<>();
					Arrays.stream(listClass.getDeclaredFields())
							// We're interested only in fields that are actually
							// present in the database (no OneToMany, no ManyToMany)
							.filter(f -> f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(ManyToOne.class))
							.forEach(f -> {
								// Re-calculate the alias that is in the database
								// query result, so we can get the appropriate fields.
								String alias = getFormattedAlias(listClass, f).split(" ")[2];
								String val = d.get(alias);
								// We can easily undo the alias generation to get
								// the field name(also can be extracted from the
								// annotation)
								String colName = resolveAliasToFieldName(listClass, alias);
								// Finally, we add the data as if it was returned
								// by the database and remove the original value from
								// the joined row.
								fkObject.put(colName, val);
								forAggregate.remove(alias);
							});
					((List<Object>)forAggregate.get(field.getName())).add(fkObject);
				}
				aggregated.put(d.get(getId()), forAggregate);
			} else {
				Map<String, Object> forAggregate = aggregated.get(id);
				for (Field field : this.oneToMany) {
					// Process is the same as for when there is no
					// data present so this can be @Refactor to a
					// method. @CopyPasta
					Class<?> listClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					Map<String, String> fkObject = new HashMap<>();
					Arrays.stream(listClass.getDeclaredFields())
							.filter(f -> f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(ManyToOne.class))
							.forEach(f -> {
								String alias = getFormattedAlias(listClass, f).split(" ")[2];
								String val = d.get(alias);
								String colName = resolveAliasToFieldName(listClass, alias);
								fkObject.put(colName, val);
								forAggregate.remove(alias);
							});
					((List<Object>)forAggregate.get(field.getName())).add(fkObject);
				}
			}
		}

		return new ArrayList<>(aggregated.values());
	}
}
