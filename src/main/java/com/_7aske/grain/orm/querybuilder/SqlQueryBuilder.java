package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.model.Model;

import java.lang.reflect.Field;
import java.util.Map;
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
	public QueryBuilder byId() {
		where = getIdValuePairs();
		return this;
	}

	// @Incomplete should allow composite keys
	@Override
	public QueryBuilder byId(Object id) {
		Field idField = getModel().getModelIds().get(0);
		where = Map.of(idField.getAnnotation(Column.class).name(), getFormattedFieldValue(idField, id));
		return this;
	}

	@Override
	public QueryBuilder where(String... whereParams) {
		where = getValuePairsFor(whereParams);
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

		switch (operation) {
			case SELECT:
				builder.append("select ");
				if (columns != null && columns.length > 0) {
					builder.append(String.join(", ", columns)).append(" ");
				} else {
					builder.append(getModel().getModelFields()
							.stream()
							.map(field ->  field.getAnnotation(Column.class).name())
							.collect(Collectors.joining(", ")));
				}
				builder.append(" from ");
				builder.append(getModel().getModelTable().name()).append(" ");
				if (where != null) {
					builder.append("where ");
					builder.append(where.entrySet().stream()
							.map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
							.collect(Collectors.joining(", ")));
					builder.append(" ");
				}
				break;
			case DELETE:
				builder.append("delete from ");
				builder.append(getModel().getModelTable().name());
				if (where != null) {
					builder.append(" where ");
					builder.append(where.entrySet().stream()
							.map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
							.collect(Collectors.joining(", ")));
					builder.append(" ");
				}
				break;
			case UPDATE:
				builder.append("update ");
				builder.append(getModel().getModelTable().name());
				builder.append(" set ");
				if (update != null) {
					builder.append(update.entrySet().stream()
							.map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
							.collect(Collectors.joining(", ")));
				}
				if (where != null) {
					builder.append(" where ");
					builder.append(where.entrySet().stream()
							.map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
							.collect(Collectors.joining(", ")));
					builder.append(" ");
				}
				break;
			case INSERT:
				builder.append("insert into ");
				builder.append(getModel().getModelTable().name());
				builder.append(" (");

				String columns = getModel().getModelFields()
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

				String values = getModel().getModelFields()
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
		return builder.toString();
	}
}
