package com._7aske.grain.util.formatter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatterTest {

	@Test
	void testFormat() {
		String res = StringFormat.format("test {}", "test");

		assertEquals("test test", res);
	}

	@Test
	void testFormat_indexedParams() {
		String res = StringFormat.format("test {1}", "test", "hello");

		assertEquals("test hello", res);
	}

	@Test
	void testFormat_indexedParamsContinuation() {
		String res = StringFormat.format("test {1} {}", "test", "hello");

		assertEquals("test hello test", res);
	}

	@Test
	void testFormat_padding() {
		String res = StringFormat.format("test {:10}", "hello");

		assertEquals("test      hello", res);
	}

	@Test
	void testFormat_paddingRight() {
		String res = StringFormat.format("test {:-10}test", "hello");

		assertEquals("test hello     test", res);
	}

	@Test
	void testFormat_decimal() {
		String res = StringFormat.format("test {:-.2}", 2.12345);
		assertEquals("test 2.12", res);
	}
}