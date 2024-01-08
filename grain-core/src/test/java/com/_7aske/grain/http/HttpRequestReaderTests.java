package com._7aske.grain.http;


import com._7aske.grain.web.http.GrainHttpRequest;
import com._7aske.grain.web.http.HttpHeaders;
import com._7aske.grain.web.server.HttpRequestReader;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com._7aske.grain.web.http.HttpConstants.CRLF;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestReaderTests {
    static final String validRequest = "GET /test HTTP/1.1" + CRLF +
                                       "Host: localhost:3000" + CRLF +
                                       "User-Agent: curl/7.78.0" + CRLF +
                                       "Accept: */*" + CRLF +
                                       "Content-Length: 7" + CRLF + CRLF +
                                       "testing";

    @Test
    void test_parseValid() throws IOException {
        BufferedInputStream reader = new BufferedInputStream(new ByteArrayInputStream(validRequest.getBytes()));

        HttpRequestReader parser = new HttpRequestReader(reader);

        GrainHttpRequest request = parser.readHttpRequest();
        assertEquals("/test", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("GET", request.getMethod().name());
        assertEquals(4, request.getHeaderNames().size());
        assertEquals("localhost:3000", request.getHeader(HttpHeaders.HOST));
        assertEquals("testing", new String(request.getInputStream().readAllBytes()));
    }

    @Test
    void test_parseMultipart() throws IOException {
        String multipartRequest = "POST /test HTTP/1.1" + CRLF +
                                  "Host: localhost:3000" + CRLF +
                                  "User-Agent: curl/7.78.0" + CRLF +
                                  "Accept: */*" + CRLF +
                                  "Content-Type: multipart/form-data; boundary=------------------------d1b1f2a1f2a1f2a1f2a1f2a1f2a1" + CRLF +
                                  "Content-Length: 7" + CRLF + CRLF +
                                  "--------------------------d1b1f2a1f2a1f2a1f2a1f2a1f2a1" + CRLF +
                                  "Content-Disposition: form-data; name=\"test\"; filename=\"test.txt\"" + CRLF +
                                  "Foo: Bar" + CRLF +
                                  "Content-Type: text/plain" + CRLF + CRLF +
                                  "testing" + CRLF +
                                  "--------------------------d1b1f2a1f2a1f2a1f2a1f2a1f2a1--";

        BufferedInputStream reader = new BufferedInputStream(new ByteArrayInputStream(multipartRequest.getBytes()));

        HttpRequestReader parser = new HttpRequestReader(reader);

        GrainHttpRequest request = parser.readHttpRequest();

        assertEquals("/test", request.getPath());
        assertEquals("POST", request.getMethod().name());
        assertEquals(5, request.getHeaderNames().size());
        assertEquals("localhost:3000", request.getHeader(HttpHeaders.HOST));
        // part
        assertEquals(1, request.getParts().size());
        assertEquals("test", request.getParts().get(0).getName());
        assertEquals("test.txt", request.getParts().get(0).getFileName());
        assertEquals("text/plain", request.getParts().get(0).getContentType());
        assertEquals("Bar", request.getParts().get(0).getHeader("Foo"));
        assertEquals("testing", new String(request.getParts().get(0).getInputStream().readAllBytes()));

    }
}
