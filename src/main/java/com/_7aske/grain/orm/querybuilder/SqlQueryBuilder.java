package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.orm.model.Model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QueryBuilder variant responsible for creating basic CRUD
 * queries for SQL based database servers.
 */
public class SqlQueryBuilder extends AbstractQueryBuilder {
	public SqlQueryBuilder(Model model) {
		super(model);
	}

	@Override
	public String getSelectQuery() {
		StringBuilder builder = new StringBuilder();
		final Table table = getModel().getModelTable();
		// @Incomplete alias should be generated more intelligently
		final String alias = table.name() + "0";

		builder.append("select").append(" ");

		builder.append(getModel().getModelFields()
				.stream()
				.map(field -> {
					Column column = field.getAnnotation(Column.class);
					return String.format("%s.%s", alias, column.name());
				})
				.collect(Collectors.joining(", ")));

		builder.append(" ").append("from").append(" ").append(table.name()).append(" ").append(alias).append(";");

		return builder.toString();
	}

	// @Todo probably should implement aliases
	@Override
	public String getUpdateQuery() {
		StringBuilder builder = new StringBuilder();
		Table table = getModel().getModelTable();

		builder.append("update").append(" ").append(table.name()).append(" ").append("set").append(" ");

		List<Field> ids = getModel().getModelIds();
		String updates = getModel().getModelFields()
				.stream()
				// Since we're updating one row in the database
				// we don't include @Id tagged attributes
				.filter(f -> !ids.contains(f))
				.map(field -> {
					Column column = field.getAnnotation(Column.class);
					return String.format("%s = %s", column.name(), getFormattedFieldValue(field));
				})
				.collect(Collectors.joining(", "));
		builder.append(updates);

		builder.append(" ").append("where").append(" ");


		String where = getModel().getModelIds()
				.stream()
				.map(field -> {
					Column column = field.getAnnotation(Column.class);
					return String.format("%s = %s", column.name(), getFormattedFieldValue(field));
				})
				.collect(Collectors.joining(" and "));
		builder.append(where).append(";");
		return builder.toString();
	}

	// @Todo probably should implement aliases
	@Override
	public String getInsertQuery() {
		StringBuilder builder = new StringBuilder();
		Table table = getModel().getModelTable();

		builder.append("insert into").append(" ").append(table.name()).append(" ").append("(");

		// @Todo only add id fields if they're auto_increment
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

		builder.append(" ").append("values").append(" ").append("(");

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
		builder.append(");");

		return builder.toString();
	}

	// @Todo probably should implement aliases
	@Override
	public String getDeleteQuery() {
		StringBuilder builder = new StringBuilder();
		Table table = getModel().getModelTable();

		builder.append("delete from ").append(table.name()).append(" ").append("where").append(" ");

		String where = getModel().getModelIds()
				.stream()
				.map(field -> {
					Column column = field.getAnnotation(Column.class);
					return String.format("%s = %s", column.name(), getFormattedFieldValue(field));
				})
				.collect(Collectors.joining(" and "));
		builder.append(where).append(";");
		return builder.toString();
	}
}
