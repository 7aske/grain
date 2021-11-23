package com._7aske.grain.orm.model;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.orm.database.DatabaseExecutor;
import com._7aske.grain.orm.querybuilder.QueryBuilder;
import com._7aske.grain.orm.querybuilder.SqlQueryBuilder;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model class representing a database entity/entry. Also contains all the required logic to generate
 * queries.
 */
public class Model {
	// @Temporary this should be injected through the dependency injection mechanism
	// instead of relying on this hack.
	private final ApplicationContext context = ApplicationContextHolder.getContext();
	private final QueryBuilder queryBuilder;

	Table table;
	List<Field> fields;
	List<Field> ids;
	protected Model() {
		// @Incomplete: error handling if models do have id's set
		// @Incomplete: error handling if models do have table annotation set
		table = getClass().getAnnotation(Table.class);
		ids = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> ReflectionUtil.isAnnotationPresent(f, Id.class))
				.collect(Collectors.toList());
		fields = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> ReflectionUtil.isAnnotationPresent(f, Column.class))
				.collect(Collectors.toList());
		queryBuilder = new SqlQueryBuilder(this);
	}

	Table getTable() {
		return table;
	}

	List<Field> getFields() {
		return fields;
	}

	List<Field> getIds() {
		return ids;
	}

	protected DatabaseExecutor getDatabaseExecutor() {
		return context.getGrainRegistry().getGrain(DatabaseExecutor.class);
	}

	// @Incomplete
	public static Model findById(Object id) {
		return null;
	}

	// @Incomplete
	public static List<Model> findAll() {
		return null;
	}

	public Model save() {
		long id = getDatabaseExecutor().executeUpdate(queryBuilder.getInsertQuery());
		if (this.getIds().size() == 1) {
			try {
				this.getIds().get(0).set(this, id);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	// @Incomplete
	public Model update() {
		return null;
	}

	// @Incomplete
	public void delete() {

	}
}
