package com._7aske.grain.http;

import com._7aske.grain.exception.http.HttpParsingException;
import com._7aske.grain.http.session.Cookie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

import static com._7aske.grain.http.HttpConstants.CRLF;
import static com._7aske.grain.http.HttpConstants.CRLF_LEN;
import static com._7aske.grain.http.HttpContentType.APPLICATION_X_WWW_FORM_URLENCODED;
import static com._7aske.grain.http.HttpHeaders.*;

public class HttpRequestParser {
	private final HttpRequest request = new HttpRequest();
	private final BufferedInputStream reader;
	private final int BYTE_BUF_SIZE = 8192;

	public HttpRequestParser(BufferedInputStream bufferedInputStream) {
		this.reader = bufferedInputStream;
	}


	public HttpRequest getHttpRequest() throws IOException {
		StringBuilder buffer = new StringBuilder();

		byte[] byteBuffer = new byte[BYTE_BUF_SIZE];
		int n;
		do {
			n = reader.read(byteBuffer);
			for (int i = 0; i < n; ++i) {
				buffer.append((char) byteBuffer[i]);
			}
		} while (n == byteBuffer.length);

		int crlfIndex = buffer.indexOf(CRLF);
		if (crlfIndex == -1) {
			throw new HttpParsingException();
		}

		String requestLineString = buffer.substring(0, crlfIndex);
		String[] requestLineParts = requestLineString.split(" ");

		if (requestLineParts.length != 3) {
			throw new HttpParsingException();
		}

		request.setMethod(HttpMethod.resolve(requestLineParts[0]));
		request.setPath(requestLineParts[1]);
		request.setVersion(requestLineParts[2]);

		int lastIndex;
		do {
			lastIndex = crlfIndex;
			crlfIndex = buffer.indexOf(CRLF, lastIndex + 1);

			// not found or start of body segment
			if (crlfIndex == -1 || lastIndex == crlfIndex - CRLF_LEN) {
				break;
			}
			String line = buffer.substring(lastIndex, crlfIndex);
			String[] headerParts = line.split(":", 2);
			if (headerParts.length != 2) {
				continue;
			}
			request.setHeader(headerParts[0].trim(), headerParts[1].trim());

		} while (true);

		if (request.hasHeader(COOKIE)) {
			request.getCookies().putAll(Cookie.parse(request.getHeader(COOKIE)));
		}

		if (lastIndex == crlfIndex - CRLF_LEN) {
			String contentLength;
			if ((contentLength = request.getHeader(CONTENT_LENGTH)) != null) {
				String body = buffer.substring(crlfIndex + CRLF_LEN, Math.min(buffer.length(), crlfIndex + CRLF_LEN + Integer.parseInt(contentLength)));
				if (Objects.equals(request.getHeader(CONTENT_TYPE), APPLICATION_X_WWW_FORM_URLENCODED)) {
					request.putParameters(body);
				} else {
					request.setBody(body);
				}
			}
		}


		return new HttpRequest(request);
	}
}
