package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.exception.GrainDbUpdateIdMissingException;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.querybuilder.helper.ModelField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import static com._7aske.grain.orm.querybuilder.QueryBuilder.Operation.*;

/**
 * QueryBuilder variant responsible for creating basic CRUD
 * queries for SQL based database servers.
 */
public class SqlQueryBuilder extends AbstractQueryBuilder {
	protected QueryBuilder.Operation operation = null;
	protected String[] columns = null;
	protected Map<String, Object> where = null;
	protected Map<String, Object> update = null;
	protected String[] groupBy = null;
	protected String[] orderBy = null;
	protected List<Join<?, ?>> joins = null;
	protected Integer pageSize = null;
	protected Integer pageNumber = null;


	public SqlQueryBuilder(Model model) {
		super(model);
	}

	@Override
	public QueryBuilder insert() {
		operation = INSERT;
		return this;
	}

	@Override
	public QueryBuilder select(String... columns) {
		operation = SELECT;
		this.columns = columns;
		return this;
	}

	@Override
	public QueryBuilder delete() {
		operation = DELETE;
		return this;
	}

	@Override
	public QueryBuilder update() {
		operation = UPDATE;
		return this;
	}

	@Override
	public QueryBuilder join() {
		joins = QueryBuilderUtil.getJoins(getModelClass(), new Stack<>());
		return this;
	}

	@Override
	public QueryBuilder join(ManyToOne relation) {
		return null;
	}

	@Override
	public QueryBuilder join(OneToMany relation) {
		return null;
	}

	@Override
	public QueryBuilder page(Integer pageNumber, Integer pageSize) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		return this;
	}

	@Override
	public QueryBuilder byId() {
		where = getIdValuePairs();
		return this;
	}

	// @Incomplete should allow composite keys
	@Override
	public QueryBuilder byId(Object id) {
		ModelField idField = getModelClass().getIdColumnField();
		where = Map.of(idField.getColumnName(), getFormattedFieldValue(idField, id));
		return this;
	}

	@Override
	public QueryBuilder where(String... whereParams) {
		where = getValuePairsFor(whereParams);
		return this;
	}

	@Override
	public QueryBuilder where(Map<String, Object> query) {
		where = query;
		return this;
	}

	@Override
	public QueryBuilder where(String field1, Object value1) {
		if (where == null)
			where = new HashMap<>();
		where.put(field1, value1);
		return this;
	}

	@Override
	public QueryBuilder where(String field1, Object value1, String field2, Object value2) {
		if (where == null)
			where = new HashMap<>();
		where.put(field1, value1);
		where.put(field2, value2);
		return this;
	}

	@Override
	public QueryBuilder where(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
		if (where == null)
			where = new HashMap<>();
		where.put(field1, value1);
		where.put(field2, value2);
		where.put(field3, value3);
		return this;
	}

	@Override
	public QueryBuilder where(String field1, Object value1, String field2, Object value2, String field3, Object value3, String field4, Object value4) {
		if (where == null)
			where = new HashMap<>();
		where.put(field1, value1);
		where.put(field2, value2);
		where.put(field3, value3);
		where.put(field4, value4);
		return this;
	}

	@Override
	public QueryBuilder groupBy(String... groupByParams) {
		this.groupBy = groupByParams;
		return this;
	}

	@Override
	public QueryBuilder orderBy(String... orderByParams) {
		this.orderBy = orderByParams;
		return this;
	}

	@Override
	public QueryBuilder allValues() {
		update = getValuePairs();
		return this;
	}

	// @Incomplete Doesn't implement group by and order by
	@Override
	public String build() {
		StringBuilder builder = new StringBuilder();
		String thisTableName = getModelClass().getTableName();

		switch (operation) {
			case SELECT:
				builder.append("select ");
				if (columns != null && columns.length > 0) {
					builder.append(String.join(", ", columns)).append(" ");
				} else {
					// @Refactor change the way model inspector returns fields
					builder.append(getModelClass().getColumnAndManyToOneFields()
							.stream()
							.map(field -> {
								String column = field.getColumnName();
								return String.format("%s.%s", thisTableName, column);
							})
							.collect(Collectors.joining(", ")));
				}
				if (joins != null && !joins.isEmpty()) {
					builder.append(", ");
					builder.append(joins.stream()
							.flatMap(j -> j.getFields().stream())
							.collect(Collectors.joining(", ")));
				}
				builder.append(" from ");
				builder.append(thisTableName).append(" ");
				if (joins != null && !joins.isEmpty()) {
					builder.append(joins.stream()
							.map(Join::getSql)
							.collect(Collectors.joining(" ")));
					builder.append(" ");
				}
				if (where != null) {
					builder.append("where ");
					builder.append(where.entrySet().stream()
							.map(this::getFormattedFieldValue)
							.collect(Collectors.joining(" and ")));
					builder.append(" ");
				}
				if (pageSize != null && pageNumber != null) {
					builder.append("limit ")
							.append(pageSize)
							.append(" offset ")
							.append(pageNumber * pageSize);
					builder.append(" ");
				}
				break;
			case DELETE:
				builder.append("delete from ");
				builder.append(thisTableName);
				if (where != null) {
					builder.append(" where ");
					builder.append(where.entrySet().stream()
							.map(this::getFormattedFieldValue)
							.collect(Collectors.joining(", ")));
					builder.append(" ");
				} else {
					throw new GrainDbUpdateIdMissingException();
				}
				break;
			case UPDATE:
				builder.append("update ");
				builder.append(thisTableName);
				builder.append(" set ");
				if (update != null) {
					// @Incomplete should probably handle cascading at some point
					builder.append(update.entrySet().stream()
							.map(this::getFormattedFieldValue)
							.collect(Collectors.joining(", ")));

				}
				if (where != null) {
					builder.append(" where ");
					builder.append(where.entrySet().stream()
							.map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
							.collect(Collectors.joining(", ")));
					builder.append(" ");
				} else {
					throw new GrainDbUpdateIdMissingException();
				}
				break;
			case INSERT:
				builder.append("insert into ");
				builder.append(thisTableName);
				builder.append(" (");

				String columns = getModelClass().getColumnFields()
						.stream()
						.filter(field -> {
							Id id = field.getAnnotation(Id.class);
							if (id == null) return true;
							return !id.autoIncrement();
						})
						.map(field -> field.getAnnotation(Column.class).name())
						.collect(Collectors.joining(", "));
				builder.append(columns);
				builder.append(")");

				builder.append(" values (");

				String values = getModelClass().getColumnFields()
						.stream()
						.filter(field -> {
							Id id = field.getAnnotation(Id.class);
							if (id == null) return true;
							return !id.autoIncrement();
						})
						.map(this::getFormattedFieldValue)
						.collect(Collectors.joining(", "));
				builder.append(values);
				builder.append(") ");
				break;
		}


		this.operation = null;
		this.columns = null;
		this.where = null;
		this.update = null;
		this.groupBy = null;
		this.orderBy = null;
		this.joins = null;
		return builder.toString();
	}
}
