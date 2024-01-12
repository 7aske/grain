package com._7aske.grain.http;


import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpHeaders;
import com._7aske.grain.web.server.HttpRequestReader;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com._7aske.grain.web.http.HttpConstants.CRLF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class HttpParserTests {
	static final String validRequest = "GET /test HTTP/1.1" + CRLF +
			"Host: localhost:3000" + CRLF +
			"User-Agent: curl/7.78.0" + CRLF +
			"Accept: */*" + CRLF +
			"Content-Length: 7" + CRLF + CRLF +
			"testing";

	@Test
	void test_parseValid() {
		BufferedInputStream reader = new BufferedInputStream(new ByteArrayInputStream(validRequest.getBytes()));

		HttpRequestReader parser = new HttpRequestReader(reader);

		try {
			HttpRequest request = parser.readHttpRequest();
			assertEquals("/test", request.getPath());
//			assertEquals("HTTP/1.1", request.getVersion());
			assertEquals("GET", request.getMethod().name());
			assertEquals(4, request.getHeaderNames().size());
			assertEquals("localhost:3000", request.getHeader(HttpHeaders.HOST));
//			assertEquals("testing", request.getBody());
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}

	}
}
