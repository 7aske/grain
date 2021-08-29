package com._7aske.grain.http;

import com._7aske.grain.exception.http.HttpParsingException;

import java.io.BufferedInputStream;
import java.io.IOException;

import static com._7aske.grain.http.HttpConstants.CRLF;
import static com._7aske.grain.http.HttpConstants.CRLF_LEN;

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

		if (lastIndex == crlfIndex - CRLF_LEN) {
			String contentLength;
			if ((contentLength = request.getHeader(HttpHeaders.CONTENT_LENGTH)) != null) {
				request.setBody(buffer.substring(crlfIndex + CRLF_LEN, Math.min(buffer.length(), crlfIndex + CRLF_LEN + Integer.parseInt(contentLength))));
			}
		}

		return new HttpRequest(request);
	}
}
