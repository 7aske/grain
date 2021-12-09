package com._7aske.grain.orm.model;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.orm.database.DatabaseExecutor;
import com._7aske.grain.orm.exception.GrainDbNoSuchRowException;
import com._7aske.grain.orm.exception.GrainDbNonUniqueResultException;
import com._7aske.grain.orm.page.Pageable;
import com._7aske.grain.orm.querybuilder.QueryBuilder;
import com._7aske.grain.orm.querybuilder.SqlQueryBuilder;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;
import com._7aske.grain.orm.querybuilder.helper.ModelField;
import com._7aske.grain.util.ReflectionUtil;

import java.util.*;

import static com._7aske.grain.util.ReflectionUtil.newInstance;

/**
 * Model class representing a database entity/entry. Also contains all the required logic to generate
 * queries.
 */
public class Model {
	// @Temporary this should be injected through the dependency injection mechanism
	// instead of relying on this hack.
	private final ApplicationContext context = ApplicationContextHolder.getContext();
	private final QueryBuilder queryBuilder;
	private final ModelClass modelClass;


	protected Model() {
		modelClass = new ModelClass(getClass());
		// @Incomplete Error handling if models do have id's set
		// @Incomplete Error handling if models do have table annotation set
		// @Incomplete Handle composite keys
		queryBuilder = new SqlQueryBuilder(this);
	}

	protected DatabaseExecutor getDatabaseExecutor() {
		return context.getGrainRegistry().getGrain(DatabaseExecutor.class);
	}

	public <T extends Model> List<T> executeQuery(Class<T> clazz, String query) {
		List<Map<String, String>> data = getDatabaseExecutor().executeQuery(query);
		ModelDataAggregator<T> aggregator = new ModelDataAggregator<>(clazz, data);
		return new ModelMapper<T>(clazz, aggregator.aggregate()).get();
	}

	public static <T extends Model> List<T> findAllBy(Class<T> clazz, Map<String, Object> params) {
		Model instance = ReflectionUtil.newInstance(clazz);
		return instance.executeQuery(clazz, instance.queryBuilder.select().where(params).build());
	}

	public static <T extends Model> T findBy(Class<T> clazz, String field1, Object value1) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		return findBy(clazz, params);
	}

	public static <T extends Model> T findBy(Class<T> clazz, String field1, Object value1, String field2, Object value2) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		params.put(field2, value2);
		return findBy(clazz, params);
	}

	public static <T extends Model> T findBy(Class<T> clazz, String field1, Object value1, String field2, Object value2, String field3, Object value3) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		params.put(field2, value2);
		params.put(field3, value3);
		return findBy(clazz, params);
	}

	public static <T extends Model> T findBy(Class<T> clazz, String field1, Object value1, String field2, Object value2, String field3, Object value3, String field4, Object value4) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		params.put(field2, value2);
		params.put(field3, value3);
		params.put(field4, value4);
		return findBy(clazz, params);
	}

	public static <T extends Model> T findBy(Class<T> clazz, Map<String, Object> params) {
		Model instance = newInstance(clazz);
		List<T> result = instance.executeQuery(clazz, instance.queryBuilder.select().where(params).build());
		if (result.size() > 1)
			throw new GrainDbNonUniqueResultException();
		if (result.isEmpty())
			throw new NoSuchElementException();
		return result.get(0);
	}

	public static <T extends Model> List<T> findAllBy(Class<T> clazz, String field1, Object value1) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		return findAllBy(clazz, params);
	}

	public static <T extends Model> List<T> findAllBy(Class<T> clazz, String field1, Object value1, String field2, Object value2) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		params.put(field2, value2);
		return findAllBy(clazz, params);
	}

	public static <T extends Model> List<T> findAllBy(Class<T> clazz, String field1, Object value1, String field2, Object value2, String field3, Object value3) {
		Map<String, Object> params = new HashMap<>();
		params.put(field1, value1);
		params.put(field2, value2);
		params.put(field3, value3);
		return findAllBy(clazz, params);
	}

	// @Incomplete
	public static <T extends Model> T findById(Class<T> clazz, Object id) {
		Model instance = newInstance(clazz);
		return instance.doFindById(clazz, id);
	}

	public static <T extends Model> List<T> findAll(Class<T> clazz, Pageable pageable) {
		Model instance = newInstance(clazz);
		return instance.doFindAll(clazz, pageable);
	}

	public static <T extends Model> List<T> findAll(Class<T> clazz) {
		Model instance = newInstance(clazz);
		return instance.doFindAll(clazz);
	}

	// @Temporary
	public <T extends Model> T doFindById(Class<T> clazz, Object id) {
		List<T> result = executeQuery(clazz, queryBuilder.select().join().byId(id).build());

		// If we're getting the rows by ID there should really be only one
		// row returned.
		if (result.size() > 1)
			throw new GrainDbNonUniqueResultException();

		if (result.size() == 0)
			throw new GrainDbNoSuchRowException();

		return result.get(0);
	}

	// @Temporary
	public <T extends Model> List<T> doFindAll(Class<T> clazz) {
		return executeQuery(clazz, queryBuilder.select().join().build());
	}

	// @Temporary
	public <T extends Model> List<T> doFindAll(Class<T> clazz, Pageable pageable) {
		return executeQuery(clazz, queryBuilder.select().join().page(pageable).build());
	}

	public <T extends Model> T save() {
		long id = getDatabaseExecutor().executeUpdate(queryBuilder.insert().build());
		// @Temporary @Incomplete handle composite keys?
		if (this.getModelClass().getIdColumnFields().size() == 1) {
			this.getModelClass().getIdColumnField().set(this, id);
		}
		return (T) this;
	}

	// @Optimization Figure out how to not query the database after updating.
	public <T extends Model> T update() {
		getDatabaseExecutor().executeUpdate(queryBuilder.update().allValues().byId().build());
		// @Incomplete handle composite keys
		// @Optimization after every update we shouldn't preform a database query
		return (T) doFindById(getClass(), this.getModelClass().getIdColumnField().get(this));
	}

	public void delete() {
		getDatabaseExecutor().executeUpdate(queryBuilder.delete().byId().build());
	}

	public ModelClass getModelClass() {
		return modelClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Model that = (Model) o;
		for (ModelField field : getModelClass().getIdColumnFields()) {
			ModelField thatField = that.getModelClass().getField(field.getField().getName());
			if (!Objects.equals(field.get(this), thatField.get(that))) return false;
		}
		return true;
	}
}
