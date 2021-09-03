package com._7aske.grain.http.json;

import org.junit.jupiter.api.Test;

class JsonObjectTest {

	@Test
	void test_toJsonString() {
		JsonObject object = new JsonObject();
		object.putString("test", "test");
		object.putBoolean("bool", true);
		JsonObject object1 = new JsonObject();
		object1.putNumber("num", 1);
		object.putObject("obj", object1);
		object.putObject("none", null);
		JsonArray array = new JsonArray();
		array.add("test");
		array.add(object1);
		array.add(14);
		array.add(null);
		array.add(true);
		object.putArray("arr", array);

		System.out.println(object.toJsonString());
	}
}