package com._7aske.grain.http.json;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonSerializerTest {
	static class Nested {
		String nested;

		@Override
		public String toString() {
			return "Nested{" +
					"nested='" + nested + '\'' +
					'}';
		}
	}

	static class TestClass {
		String field;
		Boolean bool;
		Integer num;
		Float flt;
		Nested test;
		Object nullable;
		List<Nested> list;

		public TestClass() {
		}

		@Override
		public String toString() {
			return "TestClass{" +
					"field='" + field + '\'' +
					", bool=" + bool +
					", num=" + num +
					", flt=" + flt +
					", test=" + test +
					", nullable=" + nullable +
					", list=" + list +
					'}';
		}
	}

	@Test
	void test_serialize() {
		String json = "{\"list\":[{\"nested\":\"ohyes\"}], \"field\":\"field\", \"bool\":true, \"num\":1, \"nullable\":null, \"test\": {\"nested\": \"yes\"}, \"flt\":3.3}";
		JsonObject object = new JsonParser(json).parse();
		JsonSerializer<TestClass> serializer = new JsonSerializer<>(TestClass.class);
		TestClass test = serializer.serialize(object);
		assertEquals(1, test.list.size());
		assertEquals("ohyes", test.list.get(0).nested);
		assertEquals("field", test.field);
		assertEquals(true, test.bool);
		assertEquals(1, test.num);
		assertNull(test.nullable);
		assertEquals("yes", test.test.nested);
		assertEquals(3.3f, test.flt);
	}
}