package com._7aske.grain.http.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataViewTest {

	@Test
	void test_getContent_hasCode() {
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
		assertFalse(content.contains("Commented"));
	}

	@Test
	void test_getContent_onlyVariables() {
		DataView dataView = new DataView("index-only-variables.html");
		dataView.setData("username", "should-show");
		dataView.setData("test", "this-too");
		dataView.setData("commented", "should-not-show");
		String content = dataView.getContent();
		System.out.println(content);
		assertTrue(content.contains("should-show"));
		assertTrue(content.contains("this-too"));
		assertFalse(content.contains("should-not-show"));
	}
}