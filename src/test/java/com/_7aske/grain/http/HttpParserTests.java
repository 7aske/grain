package com._7aske.grain.http;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static com._7aske.grain.http.HttpConstants.CRLF;
import static org.junit.jupiter.api.Assertions.*;

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

		HttpRequestParser parser = new HttpRequestParser(reader);

		try {
			HttpRequest request = parser.getHttpRequest();
			assertEquals("/test", request.getPath());
			assertEquals("HTTP/1.1", request.getVersion());
			assertEquals("GET", request.getMethod().name());
			assertEquals(4, request.getHeaders().size());
			assertEquals("localhost:3000", request.getHeader(HttpHeaders.HOST));
			assertEquals("testing", request.getBody());
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}

	}
}
