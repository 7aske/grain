package com._7aske.grain.http;

import java.util.HashMap;
import java.util.Map;

import static com._7aske.grain.http.HttpConstants.CRLF;
import static com._7aske.grain.http.HttpConstants.HTTP_V1;

public class HttpResponse {
	private String version = HTTP_V1;
	private HttpStatus status = HttpStatus.OK;
	private Map<String, String> headers = new HashMap<>();
	private String body = null;
	private String generatedString = null;

	public HttpResponse() {
	}
	public HttpResponse(HttpStatus status) {
		this.status = status;
	}

	public HttpResponse(HttpStatus status, String body) {
		this.status = status;
		this.body = body;
	}

	public String getHttpString() {
		if (generatedString == null) {
			StringBuilder builder = new StringBuilder();
			builder
					.append(version)
					.append(" ")
					.append(status.getValue())
					.append(" ")
					.append(status.getReason())
					.append(CRLF);
			for (Map.Entry<String, String> kv : headers.entrySet()) {
				builder.append(kv.getKey()).append(": ").append(kv.getValue()).append(CRLF);
			}
			builder.append(CRLF);
			if (body != null) {
				builder.append(body);
			}
			generatedString = builder.toString();
		}
		return generatedString;
	}

	public String getVersion() {
		return version;
	}

	public HttpResponse setVersion(String version) {
		this.version = version;
		return this;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public HttpResponse setStatus(HttpStatus status) {
		this.status = status;
		return this;
	}

	public String getHeader(String header) {
		return headers.get(header);
	}

	public HttpResponse setHeader(String header, String value) {
		headers.put(header, value);
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public HttpResponse setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		if (body == null)
			return;
		this.body = body;
		this.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length()));
	}

	public int length() {
		return getHttpString().length();
	}
}
