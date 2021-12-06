package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.page.Pageable;

import java.util.Map;

public interface QueryBuilder {
	QueryBuilder insert();
	QueryBuilder select(String... columns);
	QueryBuilder delete();
	QueryBuilder update();
	QueryBuilder join();
	QueryBuilder join(ManyToOne relation);
	QueryBuilder join(OneToMany relation);
	QueryBuilder page(Pageable pageable);

	QueryBuilder byId();
	QueryBuilder byId(Object id);
	QueryBuilder where(String... whereParams);
	QueryBuilder where(Map<String, Object> params);
	QueryBuilder where(String field1, Object value1);
	QueryBuilder where(String field1, Object value1, String field2, Object value2);
	QueryBuilder where(String field1, Object value1, String field2, Object value2, String field3, Object value3);
	QueryBuilder where(String field1, Object value1, String field2, Object value2, String field3, Object value3, String field4, Object value4);
	QueryBuilder allValues();
	QueryBuilder groupBy(String... groupByParams);
	QueryBuilder orderBy(String... orderByParams);

	String build();

	enum Operation {
		SELECT, DELETE, UPDATE, INSERT
	}

}
