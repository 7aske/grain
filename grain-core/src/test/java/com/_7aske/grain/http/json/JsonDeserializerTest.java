package com._7aske.grain.http.json;

import com._7aske.grain.http.json.annotation.JsonIgnore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonDeserializerTest {

	static class User {
		String username;

		@JsonIgnore
		String password;

		User manager;
	}

	@Test
	void test_deserialize() {
		User manager = new User();
		manager.username = "manager";
		manager.password = "bigsecret";

		User user = new User();
		user.password = "secret";
		user.username = "username";
		user.manager = manager;

		JsonSerializer<User> deserializer = new JsonSerializer<>(User.class);
		JsonObject jsonObject = (JsonObject) deserializer.serialize(user);

		assertEquals("username", jsonObject.getString("username"));
		assertNull(jsonObject.get("password"));
		assertEquals(manager.username, jsonObject.getObject("manager").getString("username"));
		assertNull(jsonObject.getObject("manager").get("password"));
		assertNull(jsonObject.getObject("manager").get("manager"));
	}
}