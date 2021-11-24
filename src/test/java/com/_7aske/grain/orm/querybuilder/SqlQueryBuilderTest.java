package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.context.ApplicationContextImpl;
import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.ManyToOne;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.orm.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlQueryBuilderTest {
	static class TestApp extends GrainApp {}
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

	@Table(name = "category")
	static class Category extends Model {
		@Id
		@Column(name = "category_id")
		private Long id;
		@Column(name = "name")
		private String name;
	}

	@Table(name = "post")
	class Post extends Model {
		@Id
		@Column(name = "post_id")
		private Long id;
		@Column(name = "title")
		private String title;
		@ManyToOne(table = "category", column = @Column(name = "category_fk"), referencedColumn = "category_id")
		private Category category;
	}


	@Table(name = "entity")
	static final class EntityWithJoin extends Model {
		@Id
		@Column(name = "test_id")
		private Integer id;

		@ManyToOne(table = "test", referencedColumn = "test_id", column = @Column(name = "test_fk"))
		private TestModel test;

	}

	TestModel testEntity;
	EntityWithJoin ewj;
	Post post;
	Category category;
	ApplicationContextImpl context;
	@BeforeEach
	void setup() {
		context = new ApplicationContextImpl(TestApp.class.getPackageName());
		ApplicationContextHolder.setContext(context);

		testEntity = new TestModel();
		testEntity.id = 1;
		testEntity.string = "Test Name";
		testEntity.number = 1;
		testEntity.date = Date.from(Instant.ofEpochSecond(0));
		testEntity.localDate = LocalDate.of(2000, 1, 1);
		testEntity.bool = true;
		testEntity.bool2 = true;

		ewj = new EntityWithJoin();
		testEntity.id = 1;
		ewj.test = testEntity;

		category = new Category();
		category.id = 1L;
		category.name = "Test Category";

		post = new Post();
		post.id = 1L;
		post.title = "Post Title";
		post.category = category;
	}


	@Test
	void testQueryBuilderSelect() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String selectSql = queryBuilder.select().build();

		assertEquals("select test_id, string, number, date, local_date, boolean, boolean2 from test ", selectSql);
	}

	@Test
	void testQueryBuilderUpdate() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String updateSql = queryBuilder.update().allValues().byId().build();

		assertEquals("update test set date = '01-01-1970', number = 1, boolean = true, string = 'Test Name', boolean2 = true, test_id = 1, local_date = '01-01-2000' where test_id = 1 ", updateSql);
	}

	@Test
	void testQueryBuilderInsert() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String insertSql = queryBuilder.insert().build();

		assertEquals("insert into test (string, number, date, local_date, boolean, boolean2) values ('Test Name', 1, '01-01-1970', '01-01-2000', true, true) ", insertSql);
	}

	@Test
	void testQueryBuilderDelete() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String deleteSql = queryBuilder.delete().byId().build();

		assertEquals("delete from test where test_id = 1 ", deleteSql);
	}

	@Test
	void testQueryBuilderSelectJoin() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(ewj);
		String deleteSql = queryBuilder.select().join().build();

		assertEquals("select test_id, test_fk, test.test_id as test_test_id, test.string as test_string, test.number as test_number, test.date as test_date, test.local_date as test_local_date, test.boolean as test_boolean, test.boolean2 as test_boolean2 from entity join test on test.test_id = entity.test_fk ", deleteSql);
	}

	@Test
	void testQueryBuilderUpdateJoin() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(post);
		String updateSql = queryBuilder.update().allValues().join().byId().build();

		assertEquals("update post set post_id = 1, title = 'Post Title', category_fk = 1 where post_id = 1 ", updateSql);
	}
}