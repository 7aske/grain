package com._7aske.grain.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private String version;
	private String path;
	private HttpMethod method;
	private Map<String, String> headers;
	private String body;

	public HttpRequest() {
		this.headers = new HashMap<>();
	}

	public HttpRequest(HttpRequest other) {
		this.version = other.version;
		this.path = other.path;
		this.method = other.method;
		this.headers = other.headers;
		this.body = other.body;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getHeader(String header) {
		return headers.get(header);
	}

	public void setHeader(String header, String value) {
		headers.put(header, value);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		return "HttpRequest{" +
				"version='" + version + '\'' +
				", path='" + path + '\'' +
				", method=" + method +
				", headers=" + headers +
				", body='" + body + '\'' +
				'}';
	}
}
