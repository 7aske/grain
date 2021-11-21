package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.orm.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlQueryBuilderTest {
	@Table(name = "test")
	public static final class TestModel extends Model {
		@Id
		@Column(name = "test_id")
		private Integer id;

		@Column(name = "string")
		private String string;

		@Column(name = "number")
		private Integer number;

		@Column(name = "date")
		private Date date;

		@Column(name = "local_date")
		private LocalDate localDate;

		@Column(name = "boolean")
		private Boolean bool;

		@Column(name = "boolean2")
		private Boolean bool2;

	}

	TestModel testEntity;

	@BeforeEach
	void setup() {
		testEntity = new TestModel();
		testEntity.id = 1;
		testEntity.string = "Test Name";
		testEntity.number = 1;
		testEntity.date = Date.from(Instant.ofEpochSecond(0));
		testEntity.localDate = LocalDate.of(2000, 1, 1);
		testEntity.bool = true;
		testEntity.bool2 = true;

	}


	@Test
	void testQueryBuilderSelect() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String selectSql = queryBuilder.getSelectQuery();

		assertEquals("select test0.test_id, test0.string, test0.number, test0.date, test0.local_date, test0.boolean from test test0;", selectSql);
	}

	@Test
	void testQueryBuilderUpdate() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String updateSql = queryBuilder.getUpdateQuery();

		assertEquals("update test set string = 'Test Name', number = 1, date = '01-01-1970', local_date = '01-01-2000', boolean = true where test_id = 1;", updateSql);
	}

	@Test
	void testQueryBuilderInsert() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String insertSql = queryBuilder.getInsertQuery();

		assertEquals("insert into test (string, number, date, local_date, boolean) values ('Test Name', 1, '01-01-1970', '01-01-2000', true);", insertSql);
	}

	@Test
	void testQueryBuilderDelete() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String deleteSql = queryBuilder.getDeleteQuery();

		assertEquals("delete from test where test_id = 1;", deleteSql);
	}
}