package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ModelDataAggregatorTest {
	@Table(name = "post")
	static class Post extends Model {
		@Id
		@Column(name = "post_id")
		private Long id;
		@Column(name = "title")
		private String title;
		@OneToMany(table = "comment", column = "comment_fk", referencedColumn = "comment_id")
		private List<Comment> comments;
	}

	@Table(name = "comment")
	static class Comment extends Model {
		@Id
		@Column(name = "comment_id")
		private Long id;
		@Column(name = "body")
		private String body;
		@ManyToOne(column = "user_fk", mappedBy = "posts")
		private User user;
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
				Map.of("user.user_id", "1", "user.name", "User", "post.post_id", "1", "post.title", "Title1", "comment.comment_id", "1", "comment.body", "Body1", "comment.post_fk", "1"),
				Map.of("user.user_id", "1", "user.name", "User", "post.post_id", "1", "post.title", "Title1", "comment.comment_id", "2", "comment.body", "Body2", "comment.post_fk", "1"),
				Map.of("user.user_id", "1", "user.name", "User", "post.post_id", "2", "post.title", "Title2", "comment.comment_id", "NULL", "comment.body", "NULL", "comment.post_fk", "NULL"));
		ModelDataAggregator<User> aggregator = new ModelDataAggregator<>(User.class, data);
		List<Map<String, Object>> result = aggregator.aggregate();
		Assertions.assertEquals("1", result.get(0).get("user_id"));
		Assertions.assertEquals("User", result.get(0).get("name"));
		Assertions.assertEquals("Title1", ((List<Map<String, Object>>) result.get(0).get("posts")).get(0).get("title"));
		Assertions.assertEquals("Title2", ((List<Map<String, Object>>) result.get(0).get("posts")).get(1).get("title"));
	}

}