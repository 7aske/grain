package com._7aske.grain.http.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTests {

	@Test
	void test_validJsonObjectParsed() {
		JsonParser deserializer = new JsonParser("{\"valid_auth\":false,\"count\":1,\"limit\":1,\"total\":139,\"last_page\":139,\"result\":[{\"id\":2582,\"cospar_id\":\"\",\"sort_date\":\"1631750396\",\"name\":\"Inspiration4\",\"provider\":{\"id\":1,\"name\":\"SpaceX\",\"slug\":\"spacex\"},\"vehicle\":{\"id\":1,\"name\":\"Falcon 9\",\"company_id\":1,\"slug\":\"falcon-9\"},\"pad\":{\"id\":2,\"name\":\"LC-39A\",\"location\":{\"id\":61,\"name\":\"Kennedy Space Center\",\"state\":\"FL\",\"statename\":\"Florida\",\"country\":\"United States\",\"slug\":\"kennedy-space-center\"}},\"missions\":[{\"id\":4026,\"name\":\"Inspiration4\",\"description\":null}],\"mission_description\":null,\"launch_description\":\"A SpaceX Falcon 9 rocket will launch the Inspiration4 mission. The launch date is currently targeted for September 15, 2021 (UTC).\",\"win_open\":null,\"t0\":null,\"win_close\":null,\"est_date\":{\"month\":9,\"day\":15,\"year\":2021,\"quarter\":null},\"date_str\":\"Sep 15\",\"tags\":[{\"id\":9,\"text\":\"Crewed\"},{\"id\":18,\"text\":\"Tourism\"}],\"slug\":\"inspiration4\",\"weather_summary\":null,\"weather_temp\":null,\"weather_condition\":null,\"weather_wind_mph\":null,\"weather_icon\":null,\"weather_updated\":null,\"quicktext\":\"Falcon 9 - Inspiration4 - Sep 15 (estimated) - https:\\/\\/rocketlaunch.live\\/launch\\/inspiration4 for info\\/stream\",\"media\":[],\"result\":-1,\"suborbital\":false,\"modified\":\"2021-03-30T14:27:13+00:00\"}], \"empty\":{}}");
		JsonObject test = deserializer.parse();
		System.out.println(test);
		assertFalse((Boolean) test.get("valid_auth"));
		assertEquals(1, test.get("count"));
		assertEquals(1, test.get("limit"));
		assertEquals(139, test.get("total"));
		assertEquals(2582, test.getArray("result").getObject(0).get("id"));
		assertEquals("", test.getArray("result").getObject(0).get("cospar_id"));
		assertEquals("1631750396", test.getArray("result").get(0, JsonObject.class).get("sort_date"));
		assertEquals("Inspiration4", test.getArray("result").get(0, JsonObject.class).get("name"));
		assertEquals(1, test.getArray("result").getObject(0).getObject("provider").get("id"));
		assertEquals("SpaceX", test.getArray("result").getObject(0).getObject("provider").get("name"));
		assertEquals(1, test.getArray("result").getObject(0).getObject("vehicle").get("id"));
		assertEquals("Falcon 9", test.getArray("result").getObject(0).getObject("vehicle").get("name"));
		assertEquals(4026, test.getArray("result").getObject(0).getArray("missions").getObject(0).get("id"));
		assertNull(test.getArray("result").getObject(0).getArray("missions").getObject(0).get("description"));
		assertEquals(18, test.getArray("result").getObject(0).getArray("tags").getObject(1).get("id"));
	}

	static final class User {
		private long id;
		private Long wid;
		private String username;

		public User() {
		}
	}

	@Test
	void testSerializer() {
		JsonDeserializer<User> serializer = new JsonDeserializer<>(User.class);
		User user = serializer.deserialize(new JsonParser("{\"username\": \"username\", \"id\": 1, \"wid\": 2}").parse());
		assertEquals(1, user.id);
		assertEquals(2, user.wid);
		assertEquals("username", user.username);
	}
}