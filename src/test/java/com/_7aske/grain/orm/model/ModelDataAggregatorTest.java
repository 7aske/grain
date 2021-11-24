package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.OneToMany;
import com._7aske.grain.orm.annotation.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com._7aske.grain.util.QueryBuilderUtil.getFormattedAlias;

class ModelDataAggregatorTest {
	@Table(name = "post")
	static class Post extends Model {
		@Id
		@Column(name = "post_id")
		private Long id;
		@Column(name = "title")
		private String title;
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

	@Test
	void testModelDataAggregator() throws NoSuchFieldException {
		List<Map<String, String>> data = List.of(
				Map.of("user_id", "1" , "name", "User", getFormattedAlias(Post.class, Post.class.getDeclaredField("id")).split(" ")[2], "1", getFormattedAlias(Post.class, Post.class.getDeclaredField("title")).split(" ")[2], "Categ1"),
				Map.of("user_id", "1" , "name", "User", getFormattedAlias(Post.class, Post.class.getDeclaredField("id")).split(" ")[2], "2", getFormattedAlias(Post.class, Post.class.getDeclaredField("title")).split(" ")[2], "Categ2"));
		ModelDataAggregator<User> aggregator = new ModelDataAggregator<>(User.class, data);
		List<Map<String, Object>> result = aggregator.aggregate();
		Assertions.assertEquals("1", result.get(0).get("user_id"));
		Assertions.assertEquals("User", result.get(0).get("name"));
		Assertions.assertEquals("Categ1", ((List<Map<String, Object>>)result.get(0).get("posts")).get(0).get("title"));
		Assertions.assertEquals("Categ2", ((List<Map<String, Object>>)result.get(0).get("posts")).get(1).get("title"));
	}

}