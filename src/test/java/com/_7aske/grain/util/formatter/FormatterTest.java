package com._7aske.grain.util.formatter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatterTest {

	@Test
	void testFormat() {
		String res = new Formatter("test {}").format("test");

		assertEquals("test test", res);
	}

	@Test
	void testFormat_indexedParams() {
		String res = new Formatter("test {1}").format("test", "hello");

		assertEquals("test hello", res);
	}

	@Test
	void testFormat_indexedParamsContinuation() {
		String res = new Formatter("test {1} {}").format("test", "hello");

		assertEquals("test hello test", res);
	}

	@Test
	void testFormat_padding() {
		String res = new Formatter("test {:10}").format("hello");

		assertEquals("test      hello", res);
	}

	@Test
	void testFormat_paddingRight() {
		String res = new Formatter("test {:-10:a}test").format("hello");

		assertEquals("test hello     test", res);
	}

	@Test
	void testFormat_decimal() {
		String res = new Formatter("test {:-.2}").format(2.12345);
		assertEquals("test 2.12", res);
	}
}