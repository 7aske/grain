package com._7aske.grain.orm.querybuilder;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.orm.annotation.*;
import com._7aske.grain.orm.model.Model;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static com._7aske.grain.orm.querybuilder.QueryBuilderUtil.getJoins;
import static org.junit.jupiter.api.Assertions.*;

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
		@ManyToOne(table = "category", column = "category_fk", referencedColumn = "category_id")
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

		@ManyToOne(table = "test", referencedColumn = "test_id", column = "test_fk")
		private TestModel test;

	}

	TestModel testEntity;
	EntityWithJoin ewj;
	Post post;
	Category category;
	ApplicationContextImpl context;
	User user;
	Movie movie;
	Screening screening;

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
		user.name = "user";
		user.posts = List.of(post);

		movie = new Movie();
		movie.id = 1L;
		movie.title = "Lord of the Rings - The Two Towers";
		movie.screenings.add(screening);

		screening = new Screening();
		screening.id = 1L;
		screening.movie = movie;
	}


	@Test
	void testQueryBuilderSelect() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(testEntity);
		String selectSql = queryBuilder.select().build();

		assertEquals("select test.test_id, test.string, test.number, test.date, test.local_date, test.boolean, test.boolean2 from test ", selectSql);
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
		String regex = "select entity.test_id, entity.test_fk, test_\\d+.test_id, test_\\d+.string, test_\\d+.number, test_\\d+.date, test_\\d+.local_date, test_\\d+.boolean, test_\\d+.boolean2 from entity left join test test_\\d+ on entity.test_fk = test_\\d+.test_id";
		Pattern pattern = Pattern.compile(regex);
		System.err.println(selectSql);
		System.err.println(pattern);
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
		String regex = "select user.user_id, user.name, post_\\d+.post_id, post_\\d+.title, post_\\d+.category_fk, category_\\d+.category_id, category_\\d+.name from user left join post post_\\d+ on user.user_id = post_\\d+.user_fk left join category category_\\d+ on post_\\d+.category_fk = category_\\d+.category_id";
		Pattern pattern = Pattern.compile(regex);
		System.err.println(selectSql);
		assertTrue(pattern.matcher(selectSql).find());
	}

	@Test
	void testNewJoins() {
		List<Join<?,?>> joins = getJoins(new ModelClass(User.class), new Stack<>());
		assertFalse(joins.isEmpty());
	}

	@Test
	void testPageable() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(category);
		String selectSql = queryBuilder.select().join().page(10, 10).build();
		String regex = "select category.category_id, category.name from category limit 10 offset 100 ";
		Pattern pattern = Pattern.compile(regex);
		System.err.println(selectSql);
		assertTrue(pattern.matcher(selectSql).matches());
	}


	@Table
	static class Screening extends Model {
		@Id
		@Column(name = "screening_id")
		private Long id;
		@Column(name = "time")
		private LocalDateTime time;
		@ManyToOne(column = "movie_fk", mappedBy = "screenings")
		private Movie movie;
	}

	@Table
	static class Movie extends Model {
		@Id
		@Column(name = "movie_id")
		private Long id;
		@Column(name = "image_url")
		private String url;
		@Column(name = "title")
		private String title;
		@Column(name = "description")
		private String description;
		@Column(name = "genre")
		private String genre;
		@Column(name = "duration")
		private Integer duration;
		@Column(name = "director")
		private String director;
		@Column(name = "release_date")
		private LocalDate releaseDate;
		@OneToMany(column = "movie_id", referencedColumn = "movie_fk", table = "screening")
		private List<Screening> screenings = new ArrayList<>();
	}

	@Test
	void testRecursiveReference() {
		QueryBuilder queryBuilder = new SqlQueryBuilder(screening);
		String selectSql = queryBuilder.select().join().build();
		System.err.println(selectSql);
	}
}