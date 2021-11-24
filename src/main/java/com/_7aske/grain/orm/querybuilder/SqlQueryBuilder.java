package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.*;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.model.ModelInspector;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com._7aske.grain.orm.querybuilder.QueryBuilder.Operation.*;
import static com._7aske.grain.util.ReflectionUtil.newInstance;

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
	protected List<Join> joins = null;

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
		joins = getModelInspector().getModelManyToOne()
				.stream()
				.map(f -> Join.from(f.getType(), f.getAnnotation(ManyToOne.class)))
				.collect(Collectors.toList());
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
					builder.append(getModelInspector().getModelFields()
							.stream()
							.map(field -> field.getAnnotation(Column.class).name())
							.collect(Collectors.joining(", ")));
					if (joins != null && !joins.isEmpty()) {
						builder.append(", ");
						builder.append(getModelInspector().getModelManyToOne()
								.stream()
								// @Refactor Was about to put aliases to make sure no collisions happen in
								// the column names but it would affect ModelMapper mapping. For
								// now this remains like this until ModelMapper mapping is changed.
								.map(field -> field.getAnnotation(ManyToOne.class).column().name())
								.collect(Collectors.joining(", ")));

						// @Refactor
						builder.append(", ");
						builder.append(joins
								.stream()
								.map(field -> {
									Class<?> clazz = field.getClazz();
									Model instance = (Model) newInstance(clazz);
									// @Incomplete should probably repeat the same process for
									// @ManyToOne fields of clazz as well???
									return new ModelInspector(instance).getModelFields()
											.stream()
											// @Temporary hack to create alias that can be used with result set metadata.
											// Alias can collide with root table column names.
											.map(f -> MessageFormat.format("{0}.{1} as {0}_{1}",
													clazz.getAnnotation(Table.class).name(),
													f.getAnnotation(Column.class).name()))
											.collect(Collectors.joining(", "));
								})
								.collect(Collectors.joining(", ")));
					}
				}
				builder.append(" from ");
				builder.append(thisTableName).append(" ");
				if (joins != null) {
					joins.forEach(join -> {
						builder.append("join ").append(join.getTable()).append(" on ");
						builder.append(join.getTable()).append(".").append(join.getReferencedColumn());
						builder.append(" = ");
						builder.append(thisTableName).append(".").append(join.getColumn()).append(" ");
					});
				}
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
				builder.append(thisTableName);
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
				builder.append(thisTableName);
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
