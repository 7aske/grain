package com._7aske.grain.util;

import org.junit.jupiter.api.Test;

import static com._7aske.grain.util.StringUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {
	@Test
	void testCamelToSnake() {
		assertEquals("camel_to_snake", camelToSnake("camelToSnake"));
		assertEquals("pascal_case", camelToSnake("PascalCase"));
		assertEquals("cconsecutive_capitals", camelToSnake("CConsecutiveCapitals"));
		assertEquals("more_sql", camelToSnake("MoreSQL"));
	}

}