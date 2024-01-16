package com._7aske.grain.web.view;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateViewTest {
	ViewResolver viewResolver;

	@BeforeEach
	void setUp() {
		viewResolver = new GtlViewResolver(Configuration.createDefault());
	}

	String readResponse(HttpResponse response) throws Exception {
		ByteArrayOutputStream baos = (ByteArrayOutputStream) response.getOutputStream();
		return baos.toString();
	}

	@Test
	void test_getContent_hasCode() throws Exception {
		TemplateView templateView = new TemplateView("index.html");
		templateView.addAttribute("username", "test");
		HttpResponse httpResponse = new GrainHttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = readResponse(httpResponse);
		System.out.println(content);
		assertTrue(content.contains("Hello test"));
		assertTrue(content.contains("Logout"));
		assertTrue(content.contains("Commented"));
	}

	@Test
	void test_getContent_noCode() throws Exception {
		TemplateView templateView = new TemplateView("index-no-code.html");
		HttpResponse httpResponse = new GrainHttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = readResponse(httpResponse);
		assertTrue(content.contains("Log in"));
		assertTrue(content.contains("Logout"));
		assertFalse(content.contains("Commented"));
	}

	@Test
	void test_getContent_onlyVariables() throws Exception {
		TemplateView templateView = new TemplateView("index-only-variables.html");
		templateView.addAttribute("username", "should-show");
		templateView.addAttribute("test", "this-too");
		templateView.addAttribute("commented", "should-not-show");
		HttpResponse httpResponse = new GrainHttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = readResponse(httpResponse);
		System.out.println(content);
		assertTrue(content.contains("should-show"));
		assertTrue(content.contains("this-too"));
		assertFalse(content.contains("should-not-show"));
	}

	@Test
	void test_getContent_simpleExpression() throws Exception {
		TemplateView templateView = new TemplateView("index-simple-expression.html");
		HttpResponse httpResponse = new GrainHttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = readResponse(httpResponse);
		System.out.println(content);
		assertTrue(content.contains("Result is:20"));
		assertTrue(content.contains("String is:test"));
	}

	@Test
	void test_include() throws Exception {
		TemplateView templateView = new TemplateView("include.html");
		HttpResponse httpResponse = new GrainHttpResponse();
		viewResolver.resolve(templateView, null, httpResponse, null, null);
		String content = readResponse(httpResponse);
		assertTrue(content.contains("<div class=\"container\">"));
		assertTrue(content.contains("<div>this is a card</div>"));
	}
}