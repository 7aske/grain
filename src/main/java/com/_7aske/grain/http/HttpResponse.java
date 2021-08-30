package com._7aske.grain.http;

import java.util.HashMap;
import java.util.Map;

import static com._7aske.grain.http.HttpConstants.CRLF;
import static com._7aske.grain.http.HttpConstants.HTTP_V1;

public class HttpResponse {
	private String version = HTTP_V1;
	private HttpStatus status;
	private Map<String, String> headers = new HashMap<>();
	private String body = null;

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
		return builder.toString();

	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getHeader(String header) {
		return headers.get(header);
	}

	public void setHeader(String header, String value) {
		headers.put(header, value);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
