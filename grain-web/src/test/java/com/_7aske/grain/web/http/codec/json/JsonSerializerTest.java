package com._7aske.grain.web.http.codec.json;

import java.util.List;

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
		Long lng;
		Float flt;
		Double dbl;
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

//	@Test
//	void test_serialize() {
//		String json = "{\"list\":[{\"nested\":\"ohyes\"}], \"field\":\"field\", \"bool\":true, \"num\":1, \"nullable\":null, \"test\": {\"nested\": \"yes\"}, \"flt\":3.3, \"lng\": 1, \"dbl\": 0.1242}";
//		JsonObject object = new JsonParser(json).parse();
//		JsonDeserializer<TestClass> serializer = new JsonDeserializer<>(TestClass.class);
//		TestClass test = serializer.deserialize(object);
//		assertEquals(1, test.list.size());
//		assertEquals("ohyes", test.list.get(0).nested);
//		assertEquals("field", test.field);
//		assertEquals(true, test.bool);
//		assertEquals(1, test.num);
//		assertNull(test.nullable);
//		assertEquals("yes", test.test.nested);
//		assertEquals(3.3f, test.flt);
//	}
}