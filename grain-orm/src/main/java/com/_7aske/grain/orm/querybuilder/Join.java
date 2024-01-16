package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.model.Model;

import java.util.List;
import java.util.stream.Collectors;

import static com._7aske.grain.orm.querybuilder.QueryBuilderUtil.getAlias;

public class Join<S extends Model, T extends Model> {
	private String sourceAlias;
	private String targetAlias;
	private String targetTable;
	private String sourceColumn;
	private String targetColumn;
	private List<String> fields;

	private Join() {
	}

	public static <S extends Model, T extends Model> Join<S, T> from(String source, String sourceColumn, String target, String targetColumn, List<String> fields) {
		Join<S, T> self = new Join<>();
		self.sourceAlias = source;
		self.targetTable = target;
		self.sourceColumn = sourceColumn;
		self.targetColumn = targetColumn;
		self.fields = fields;
		return self;
	}

	public String alias() {
		return targetAlias != null ? targetAlias : getAlias(targetTable);
	}

	public List<String> getFields() {
		return fields.stream().map(f -> String.format("%s.%s", alias(), f)).collect(Collectors.toList());
	}

	public String getSql() {
		String target = targetAlias != null ? targetAlias : getAlias(targetTable);
		return String.format("left join %s %s on %s", targetTable, target, getJoinColumns());
	}

	private String getJoinColumns() {
		String target = targetAlias != null ? targetAlias : getAlias(targetTable);
		return String.format("%s.%s = %s.%s", sourceAlias, sourceColumn, target, targetColumn);
	}
}
