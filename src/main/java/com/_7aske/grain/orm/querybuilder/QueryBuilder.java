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

	class Join {
		private final String table;
		private final String column;
		private final String referencedColumn;
		private final Class<?> clazz;

		public static Join from(Class<?> clazz, OneToMany oneToMany) {
			return new Join(clazz, oneToMany.table(), oneToMany.column(), oneToMany.referencedColumn());
		}

		public static Join from(Class<?> clazz, ManyToOne manyToOne) {
			return new Join(clazz, manyToOne.table(), manyToOne.column().name(), manyToOne.referencedColumn());
		}

		public Join(Class<?> clazz, String table, String column, String referencedColumn) {
			this.clazz = clazz;
			this.column = column;
			this.referencedColumn = referencedColumn;
			this.table = table;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public String getColumn() {
			return column;
		}

		public String getReferencedColumn() {
			return referencedColumn;
		}

		public String getTable() {
			return table;
		}
	}
}
