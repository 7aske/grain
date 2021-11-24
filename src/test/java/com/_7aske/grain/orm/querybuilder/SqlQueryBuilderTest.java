package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.context.ApplicationContextImpl;
import com._7aske.grain.orm.annotation.*;
import com._7aske.grain.orm.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlQueryBuilderTest {
	static class TestApp extends GrainApp {
	}

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
	static class Post extends Model {
		@Id
		@Column(name = "post_id")
		private Long id;
		@Column(name = "title")
		private String title;
		@ManyToOne(table = "category", column = @Column(name = "category_fk"), referencedColumn = "category_id")
		private Category category;
		// @ManyToOne(table = "user", referencedColumn = "user_id", column = @Column(name = "user_fk"))
		// private User user;
	}

	@Table(name = "user")
	static class User extends Model {
		@Id
		@Column(name = "user_id")
		private Long id;
		@Column(name = "name")
		private String name;
		@OneToMany(table = "post", column = "user_id", referencedColumn = "user_fk")
		private List<Post> posts;
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
	User user;

	@BeforeEach
	void setup() {
		// Reloading context
		ApplicationContextHolder.setContext(null);
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

		user = new User();
		user.id = 1L;
		user.name= "user";
		user.posts = List.of(post);
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
		String selectSql = queryBuilder.select().join().build();
		String regex = "select test_id, test_fk, test.test_id as test_\\d+_test_id, test.string as test_\\d+_string, test.number as test_\\d+_number, test.date as test_\\d+_date, test.local_date as test_\\d+_local_date, test.boolean as test_\\d+_boolean, test.boolean2 as test_\\d+_boolean2 from entity join test on test.test_id = entity.test_fk ";
		Pattern pattern = Pattern.compile(regex);
		System.err.println(selectSql);
		assertTrue(pattern.matcher(selectSql).find());
	}

	@Test
	void testQueryBuilderUpdateJoin() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(post);
		String updateSql = queryBuilder.update().allValues().join().byId().build();
		assertEquals("update post set post_id = 1, title = 'Post Title', category_fk = 1 where post_id = 1 ", updateSql);
	}

	@Test
	void testQueryBuilderSelectOneToManyJoin() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(user);
		String selectSql = queryBuilder.select().join().build();
		String regex = "select user_id, name, post.post_id as post_\\d+_post_id, post.title as post_\\d+_title from user join post on post.user_fk = user.user_id ";
		Pattern pattern = Pattern.compile(regex);
		System.err.println(selectSql);
		assertTrue(pattern.matcher(selectSql).find());
	}
}