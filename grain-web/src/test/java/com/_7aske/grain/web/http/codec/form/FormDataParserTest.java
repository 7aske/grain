package com._7aske.grain.web.http.codec.form;

import com._7aske.grain.web.http.codec.form.FormDataParser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FormDataParserTest {
	@Test
	void testParsingFormData() {
		String body = "test1=test1&test2=test2";
		FormDataParser formDataParser = new FormDataParser(body);
		Map<String, String> parsed = formDataParser.parse();

		assertEquals("test1", parsed.get("test1"));
		assertEquals("test2", parsed.get("test2"));
	}

	@Test
	void testParsingFormData_urlencoded() {
		String body = String.format("test1=%s&test2=%s", encode("test=15", UTF_8), encode("%15", UTF_8));
		FormDataParser formDataParser = new FormDataParser(body);
		Map<String, String> parsed = formDataParser.parse();

		assertEquals("test=15", parsed.get("test1"));
		assertEquals("%15", parsed.get("test2"));
	}
}