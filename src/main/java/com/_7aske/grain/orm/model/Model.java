package com._7aske.grain.orm.model;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.orm.annotation.*;
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

import static com._7aske.grain.util.ReflectionUtil.getAnyConstructor;
import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

/**
 * Model class representing a database entity/entry. Also contains all the required logic to generate
 * queries.
 */
public class Model {
	// @Temporary this should be injected through the dependency injection mechanism
	// instead of relying on this hack.
	private final ApplicationContext context = ApplicationContextHolder.getContext();
	private final QueryBuilder queryBuilder;

	final Table table;
	final List<Field> fields;
	final List<Field> ids;
	final List<Field> oneToMany;
	final List<Field> manyToOne;

	protected Model() {
		// @Incomplete Error handling if models do have id's set
		// @Incomplete Error handling if models do have table annotation set
		// @Incomplete Handle composite keys
		table = getClass().getAnnotation(Table.class);
		ids = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, Id.class))
				.collect(Collectors.toList());
		fields = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> ReflectionUtil.isAnnotationPresent(f, Column.class))
				.collect(Collectors.toList());
		oneToMany = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, OneToMany.class))
				.collect(Collectors.toList());
		manyToOne = Arrays.stream(getClass().getDeclaredFields())
				.filter(f -> isAnnotationPresent(f, ManyToOne.class))
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

	List<Field> getOneToMany() {
		return oneToMany;
	}

	List<Field> getManyToOne() {
		return manyToOne;
	}

	protected DatabaseExecutor getDatabaseExecutor() {
		return context.getGrainRegistry().getGrain(DatabaseExecutor.class);
	}

	// @Incomplete
	public static <T extends Model> T findById(Class<T> clazz, Object id) {
		try {
			Model instance = getAnyConstructor(clazz).newInstance();
			return instance.doFindById(clazz, id);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T extends Model> List<T> findAll(Class<T> clazz) {
		try {
			Model instance = getAnyConstructor(clazz).newInstance();
			return instance.doFindAll(clazz);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// @Temporary
	public <T extends Model> T doFindById(Class<T> clazz, Object id) {
		DatabaseExecutor databaseExecutor = getDatabaseExecutor();
		List<Map<String, String>> data = databaseExecutor.executeQuery(queryBuilder.select().join().byId(id).build());
		ModelMapper<T> modelMapper = new ModelMapper<>(clazz, data);
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
	public <T extends Model> List<T> doFindAll(Class<T> clazz) {
		DatabaseExecutor databaseExecutor = getDatabaseExecutor();
		List<Map<String, String>> data = databaseExecutor.executeQuery(queryBuilder.select().join().build());
		ModelMapper<T> modelMapper = new ModelMapper<>(clazz, data);
		return modelMapper.get();
	}

	public <T extends Model> T save() {
		long id = getDatabaseExecutor().executeUpdate(queryBuilder.insert().build());
		// @Temporary @Incomplete handle composite keys?
		if (this.getIds().size() == 1) {
			try {
				this.getIds().get(0).set(this, id);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return (T) this;
	}

	// @Optimization Figure out how to not query the database after updating.
	public <T extends Model> T update() {
		getDatabaseExecutor().executeUpdate(queryBuilder.update().allValues().byId().build());
		// @Incomplete handle composite keys
		Field idField = getIds().get(0);
		try {
			return (T) doFindById(this.getClass(), idField.get(this));
		} catch (IllegalAccessException e) {
			// @Warning If the findById query fails we return same object we updated
			// there might be loss of information in that case.
			e.printStackTrace();
			return (T) this;
		}
	}

	public void delete() {
		getDatabaseExecutor().executeUpdate(queryBuilder.delete().byId().build());
	}
}
