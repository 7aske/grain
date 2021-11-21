package com._7aske.grain.orm.querybuilder;

public interface QueryBuilder {
	String getSelectQuery();
	String getUpdateQuery();
	String getInsertQuery();
	String getDeleteQuery();
}
