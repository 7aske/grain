package com._7aske.grain.http.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateViewTest {

	@Test
	void test_getContent_hasCode() {
		TemplateView templateView = new TemplateView("index.html");
		templateView.setData("username", "test");
		String content = templateView.getContent();
		System.out.println(content);
		assertTrue(content.contains("Hello test"));
		assertTrue(content.contains("Logout"));
		assertTrue(content.contains("Commented"));
	}

	@Test
	void test_getContent_noCode() {
		TemplateView templateView = new TemplateView("index-no-code.html");
		String content = templateView.getContent();
		assertTrue(content.contains("Log in"));
		assertTrue(content.contains("Logout"));
		assertFalse(content.contains("Commented"));
	}

	@Test
	void test_getContent_onlyVariables() {
		TemplateView templateView = new TemplateView("index-only-variables.html");
		templateView.setData("username", "should-show");
		templateView.setData("test", "this-too");
		templateView.setData("commented", "should-not-show");
		String content = templateView.getContent();
		System.out.println(content);
		assertTrue(content.contains("should-show"));
		assertTrue(content.contains("this-too"));
		assertFalse(content.contains("should-not-show"));
	}

	@Test
	void test_getContent_simpleExpression() {
		TemplateView templateView = new TemplateView("index-simple-expression.html");
		String content = templateView.getContent();
		System.out.println(content);
		assertTrue(content.contains("Result is:20"));
		assertTrue(content.contains("String is:test"));
	}

	@Test
	void test_include() {
		TemplateView templateView = new TemplateView("include.html");
		String content = templateView.getContent();
		assertTrue(content.contains("<div class=\"container\">"));
		assertTrue(content.contains("<div>this is a card</div>"));
	}
}