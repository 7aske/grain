package com._7aske.grain.http.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DataViewTest {

	@Test
	void test_getContent() {
		DataView dataView = new DataView("index.html");
		dataView.setData("username", "test");
		String content = dataView.getContent();
		System.out.println(content);
		assertTrue(content.contains("Hello test"));
		assertTrue(content.contains("Logout"));
		assertTrue(content.contains("Commented"));
	}

	@Test
	void test_getContent_noCode() {
		DataView dataView = new DataView("index-no-code.html");
		String content = dataView.getContent();
		assertTrue(content.contains("Log in"));
		assertTrue(content.contains("Logout"));
		assertTrue(content.contains("Commented"));
	}
}