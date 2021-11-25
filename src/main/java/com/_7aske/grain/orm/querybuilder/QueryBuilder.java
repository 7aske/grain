package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;

public interface QueryBuilder {
	QueryBuilder insert();
	QueryBuilder select(String... columns);
	QueryBuilder delete();
	QueryBuilder update();
	QueryBuilder join();
	QueryBuilder join(ManyToOne relation);
	QueryBuilder join(OneToMany relation);

	QueryBuilder byId();
	QueryBuilder byId(Object id);
	QueryBuilder where(String... whereParams);
	QueryBuilder allValues();
	QueryBuilder groupBy(String... groupByParams);
	QueryBuilder orderBy(String... orderByParams);

	String build();

	enum Operation {
		SELECT, DELETE, UPDATE, INSERT
	}

}
