package com._7aske.grain.http;

import com._7aske.grain.exception.http.HttpParsingException;

import java.io.BufferedInputStream;
import java.io.IOException;

public class HttpRequestParser {
	private final HttpRequest request = new HttpRequest();
	private final BufferedInputStream reader;

	public HttpRequestParser(BufferedInputStream bufferedInputStream) {
		this.reader = bufferedInputStream;
	}


	public HttpRequest getHttpRequest() throws IOException {
		StringBuilder buffer = new StringBuilder();

		byte[] byteBuffer = new byte[8192];
		int n;
		do {
			n = reader.read(byteBuffer);
			for (int i = 0; i < n; ++i) {
				buffer.append((char) byteBuffer[i]);
			}
		} while (n == byteBuffer.length);

		int crlfIndex = buffer.indexOf("\r\n");
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
			crlfIndex = buffer.indexOf("\r\n", lastIndex + 1);

			// not found or start of body segment
			if (crlfIndex == -1 || lastIndex == crlfIndex - 2) {
				break;
			}
			String line = buffer.substring(lastIndex, crlfIndex);
			String[] headerParts = line.split(":");
			if (headerParts.length != 2) {
				continue;
			}
			request.setHeader(headerParts[0].trim(), headerParts[1].trim());

		} while (true);

		if (lastIndex == crlfIndex - 2) {
			String contentLength;
			if ((contentLength = request.getHeader("Content-Length")) != null) {
				request.setBody(buffer.substring(crlfIndex + 2, Math.min(buffer.length(), crlfIndex + 2 + Integer.parseInt(contentLength))));
			}
		}

		return new HttpRequest(request);
	}
}
