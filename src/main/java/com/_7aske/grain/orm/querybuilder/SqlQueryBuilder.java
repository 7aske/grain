package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.exception.GrainDbUpdateIdMissingException;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.page.Pageable;
import com._7aske.grain.util.QueryBuilderUtil;

import java.lang.reflect.Field;
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
	protected Pageable pageable;

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
		joins = QueryBuilderUtil.getJoins(getModelInspector().getModel().getClass(), new Stack<>());
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
	public QueryBuilder page(Pageable pageable) {
		this.pageable = pageable;
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
		Field idField = getModelInspector().getModelIds().get(0);
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
		String thisTableName = getModelInspector().getModelTable().name();

		switch (operation) {
			case SELECT:
				builder.append("select ");
				if (columns != null && columns.length > 0) {
					builder.append(String.join(", ", columns)).append(" ");
				} else {
					// @Refactor change the way model inspector returns fields
					builder.append(getModelInspector().getAllModelFields()
							.stream()
							.map(field -> {
								String column;
								if (field.isAnnotationPresent(Column.class)) {
									column = field.getAnnotation(Column.class).name();
								} else {
									column = field.getAnnotation(ManyToOne.class).column();
								}
								return String.format("%s.%s", thisTableName, column);
							})
							.collect(Collectors.joining(", ")));
					if (joins != null && !joins.isEmpty()) {
						builder.append(", ");
						builder.append(joins.stream()
								.flatMap(j -> j.getFields().stream())
								.collect(Collectors.joining(", ")));
					}
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
							.map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
							.collect(Collectors.joining(", ")));
					builder.append(" ");
				}
				if (pageable != null) {
					builder.append("limit ")
							.append(pageable.getCount())
							.append(" offset ")
							.append(pageable.getPage() * pageable.getCount());
					builder.append(" ");
				}
				break;
			case DELETE:
				builder.append("delete from ");
				builder.append(thisTableName);
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
			case UPDATE:
				builder.append("update ");
				builder.append(thisTableName);
				builder.append(" set ");
				if (update != null) {
					// @Incomplete should probably handle cascading at some point
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
				} else {
					throw new GrainDbUpdateIdMissingException();
				}
				break;
			case INSERT:
				builder.append("insert into ");
				builder.append(thisTableName);
				builder.append(" (");

				String columns = getModelInspector().getModelFields()
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

				String values = getModelInspector().getModelFields()
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
