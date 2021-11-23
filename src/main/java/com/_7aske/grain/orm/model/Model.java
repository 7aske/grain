package com._7aske.grain.orm.model;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.orm.database.DatabaseExecutor;
import com._7aske.grain.orm.exception.GrainDbNoSuchRowException;
import com._7aske.grain.orm.exception.GrainDbNonUniqueResultException;
import com._7aske.grain.orm.querybuilder.QueryBuilder;
import com._7aske.grain.orm.querybuilder.SqlQueryBuilder;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
		// @Incomplete Error handling if models do have id's set
		// @Incomplete Error handling if models do have table annotation set
		// @Incomplete Handle composite keys
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
	public static <T extends Model> T findById(Class<T> clazz, Object id) {
		try {
			Model instance = ReflectionUtil.getAnyConstructor(clazz).newInstance();
			return (T) instance.doFindById(clazz, id);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T extends Model> List<T> findAll(Class<T> clazz) {
		try {
			Model instance = ReflectionUtil.getAnyConstructor(clazz).newInstance();
			return (List<T>) instance.doFindAll(clazz);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// @Temporary
	public Model doFindById(Class<? extends Model> clazz, Object id) {
		DatabaseExecutor databaseExecutor = getDatabaseExecutor();
		List<Map<String, Object>> data = databaseExecutor.executeQuery(queryBuilder.select().byId(id).build());
		ModelMapper modelMapper = new ModelMapper(clazz, data);
		List<? extends Model> result = modelMapper.get();
		// If we're getting the rows by ID there should really be only one
		// row returned.
		if (result.size() > 1)
			throw new GrainDbNonUniqueResultException();

		if (result.size() == 0)
			throw new GrainDbNoSuchRowException();

		return modelMapper.get().get(0);
	}

	// @Temporary
	public List<Model> doFindAll(Class<? extends Model> clazz) {
		DatabaseExecutor databaseExecutor = getDatabaseExecutor();
		List<Map<String, Object>> data = databaseExecutor.executeQuery(queryBuilder.select().build());
		ModelMapper modelMapper = new ModelMapper(clazz, data);
		return modelMapper.get();
	}

	public Model save() {
		long id = getDatabaseExecutor().executeUpdate(queryBuilder.insert().build());
		// @Temporary @Incomplete handle composite keys?
		if (this.getIds().size() == 1) {
			try {
				this.getIds().get(0).set(this, id);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	// @Optimization Figure out how to not query the database after updating.
	public Model update() {
		getDatabaseExecutor().executeQuery(queryBuilder.update().allValues().byId().build());
		// @Incomplete handle composite keys
		Field idField = getIds().get(0);
		try {
			return doFindById(this.getClass(), idField.get(this));
		} catch (IllegalAccessException e) {
			// @Warning If the findById query fails we return same object we updated
			// there might be loss of information in that case.
			e.printStackTrace();
			return this;
		}
	}

	public void delete() {
		getDatabaseExecutor().executeUpdate(queryBuilder.delete().byId().build());
	}
}
