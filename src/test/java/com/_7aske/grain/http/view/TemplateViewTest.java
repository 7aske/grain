package com._7aske.grain.http.view;

import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.web.view.GtlViewResolver;
import com._7aske.grain.web.view.TemplateView;
import com._7aske.grain.web.view.ViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateViewTest {
	ViewResolver viewResolver;

	@BeforeEach
	void setUp() {
		viewResolver = new GtlViewResolver();
	}

	@Test
	void test_getContent_hasCode() {
		TemplateView templateView = new TemplateView("index.html");
		templateView.addAttribute("username", "test");
		HttpResponse httpResponse = new HttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = httpResponse.getBody();
		System.out.println(content);
		assertTrue(content.contains("Hello test"));
		assertTrue(content.contains("Logout"));
		assertTrue(content.contains("Commented"));
	}

	@Test
	void test_getContent_noCode() {
		TemplateView templateView = new TemplateView("index-no-code.html");
		HttpResponse httpResponse = new HttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = httpResponse.getBody();
		assertTrue(content.contains("Log in"));
		assertTrue(content.contains("Logout"));
		assertFalse(content.contains("Commented"));
	}

	@Test
	void test_getContent_onlyVariables() {
		TemplateView templateView = new TemplateView("index-only-variables.html");
		templateView.addAttribute("username", "should-show");
		templateView.addAttribute("test", "this-too");
		templateView.addAttribute("commented", "should-not-show");
		HttpResponse httpResponse = new HttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = httpResponse.getBody();
		System.out.println(content);
		assertTrue(content.contains("should-show"));
		assertTrue(content.contains("this-too"));
		assertFalse(content.contains("should-not-show"));
	}

	@Test
	void test_getContent_simpleExpression() {
		TemplateView templateView = new TemplateView("index-simple-expression.html");
		HttpResponse httpResponse = new HttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = httpResponse.getBody();
		System.out.println(content);
		assertTrue(content.contains("Result is:20"));
		assertTrue(content.contains("String is:test"));
	}

	@Test
	void test_include() {
		TemplateView templateView = new TemplateView("include.html");
		HttpResponse httpResponse = new HttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = httpResponse.getBody();
		assertTrue(content.contains("<div class=\"container\">"));
		assertTrue(content.contains("<div>this is a card</div>"));
	}
}