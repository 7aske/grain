package com._7aske.grain.http;

import com._7aske.grain.http.session.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.Collectors;

import static com._7aske.grain.http.HttpConstants.*;

public class HttpResponseWriter {
	private final HttpResponse response;


	public HttpResponseWriter(HttpResponse response) {
		this.response = response;
	}

	public void writeTo(OutputStream outputStream) throws IOException {
		outputStream.write(response.getVersion().getBytes());
		outputStream.write(SPACE);
		outputStream.write(String.valueOf(response.getStatus().getValue()).getBytes());
		outputStream.write(SPACE);
		outputStream.write(response.getStatus().getReason().getBytes());
		outputStream.write(CRLF_BYTES);

		if (!response.getCookies().isEmpty()) {
			String cookieValue = response.getCookies().values()
					.stream()
					.map(Cookie::toString)
					.collect(Collectors.joining(""));
			response.getHeaders().put(HttpHeaders.SET_COOKIE, cookieValue);
		}

		ByteArrayOutputStream baos = response.getByteArrayOutputStream();

		if (baos.size() > 0) {
			response.getHeaders()
					.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(baos.size()));
		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> kv : response.getHeaders().entrySet()) {
			builder.append(kv.getKey()).append(": ").append(kv.getValue()).append(CRLF);
			outputStream.write(builder.toString().getBytes());
			builder.setLength(0);
		}

		outputStream.write(CRLF_BYTES);

		if (baos.size() > 0) {
			baos.writeTo(outputStream);
		}

		outputStream.flush();
		outputStream.close();
	}

	public HttpResponse getResponse() {
		return response;
	}
}
