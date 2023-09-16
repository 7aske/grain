package com._7aske.grain.http.json;

import com._7aske.grain.http.json.nodes.JsonNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTests {

	@Test
	void test_validJsonObjectParsed() {
		JsonParser deserializer = new JsonParser();
		String json = "{\"valid_auth\":false,\"count\":1,\"limit\":1,\"total\":139,\"last_page\":139,\"result\":[{\"id\":2582,\"cospar_id\":\"\",\"sort_date\":\"1631750396\",\"name\":\"Inspiration4\",\"provider\":{\"id\":1,\"name\":\"SpaceX\",\"slug\":\"spacex\"},\"vehicle\":{\"id\":1,\"name\":\"Falcon 9\",\"company_id\":1,\"slug\":\"falcon-9\"},\"pad\":{\"id\":2,\"name\":\"LC-39A\",\"location\":{\"id\":61,\"name\":\"Kennedy Space Center\",\"state\":\"FL\",\"statename\":\"Florida\",\"country\":\"United States\",\"slug\":\"kennedy-space-center\"}},\"missions\":[{\"id\":4026,\"name\":\"Inspiration4\",\"description\":null}],\"mission_description\":null,\"launch_description\":\"A SpaceX Falcon 9 rocket will launch the Inspiration4 mission. The launch date is currently targeted for September 15, 2021 (UTC).\",\"win_open\":null,\"t0\":null,\"win_close\":null,\"est_date\":{\"month\":9,\"day\":15,\"year\":2021,\"quarter\":null},\"date_str\":\"Sep 15\",\"tags\":[{\"id\":9,\"text\":\"Crewed\"},{\"id\":18,\"text\":\"Tourism\"}],\"slug\":\"inspiration4\",\"weather_summary\":null,\"weather_temp\":null,\"weather_condition\":null,\"weather_wind_mph\":null,\"weather_icon\":null,\"weather_updated\":null,\"quicktext\":\"Falcon 9 - Inspiration4 - Sep 15 (estimated) - https:\\/\\/rocketlaunch.live\\/launch\\/inspiration4 for info\\/stream\",\"media\":[],\"result\":-1,\"suborbital\":false,\"modified\":\"2021-03-30T14:27:13+00:00\"}], \"empty\":{}}";
		JsonNode test = deserializer.parse(json);
		System.out.println(test);
		assertFalse(test.get("valid_auth").getBoolean());
		assertEquals(1L, test.get("count").getValue());
		assertEquals(1L, test.get("limit").getValue());
		assertEquals(139L, test.get("total").getValue());
		assertEquals(2582L, test.get("result").getArray().get(0).get("id").getValue());
		assertEquals("", test.get("result").getArray().get(0).get("cospar_id").getValue());
		assertEquals("1631750396", test.get("result").getArray().get(0).get("sort_date").getValue());
		assertEquals("Inspiration4", test.get("result").getArray().get(0).get("name").getValue());
		assertEquals(1L, test.get("result").getArray().get(0).get("provider").get("id").getValue());
		assertEquals("SpaceX", test.get("result").getArray().get(0).get("provider").get("name").getValue());
		assertEquals(1L, test.get("result").getArray().get(0).get("vehicle").get("id").getValue());
		assertEquals("Falcon 9", test.get("result").getArray().get(0).get("vehicle").get("name").getValue());
		assertEquals(4026L, test.get("result").getArray().get(0).get("missions").getArray().get(0).get("id").getValue());
		assertNull(test.get("result").getArray().get(0).get("missions").getArray().get(0).get("description").getValue());
		assertEquals(18L, test.get("result").getArray().get(0).get("tags").getArray().get(1).get("id").getValue());
	}

	static final class User {
		private long id;
		private Long wid;
		private String username;

		public User() {
		}
	}
}