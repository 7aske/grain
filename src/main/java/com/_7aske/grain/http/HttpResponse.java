package com._7aske.grain.http;

import com._7aske.grain.http.session.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com._7aske.grain.http.HttpConstants.CRLF;
import static com._7aske.grain.http.HttpConstants.HTTP_V1;

public class HttpResponse {
	private final Map<String, String> headers;
	private String version = HTTP_V1;
	private HttpStatus status;
	private String body;
	private String cachedHttpString;
	private final Map<String, Cookie> cookies;

	public HttpResponse() {
		this(HttpStatus.OK, null);
	}

	public HttpResponse(HttpStatus status) {
		this(status, null);
	}

	public HttpResponse(HttpStatus status, String body) {
		this.status = status;
		this.body = body;
		this.headers = new HashMap<>();
		this.cookies = new HashMap<>();
		this.cachedHttpString = null;
	}

	public String getHttpString() {
		if (cachedHttpString == null) {
			StringBuilder builder = new StringBuilder();
			builder.append(version)
					.append(" ")
					.append(status.getValue())
					.append(" ")
					.append(status.getReason())
					.append(CRLF);

			if (!cookies.isEmpty()) {
				headers.put(HttpHeaders.SET_COOKIE, cookies.values().stream().map(Cookie::toString).collect(Collectors.joining("")));
			}

			for (Map.Entry<String, String> kv : headers.entrySet()) {
				builder.append(kv.getKey()).append(": ").append(kv.getValue()).append(CRLF);
			}

			builder.append(CRLF);
			if (body != null) {
				builder.append(body);
			}
			cachedHttpString = builder.toString();
		}
		return cachedHttpString;
	}

	public void setCookie(Cookie cookie) {
		this.cookies.put(cookie.getName(), cookie);
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

	public HttpResponse removeHeader(String header) {
		headers.remove(header);
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public HttpResponse addHeaders(Map<String, String> headers) {
		if (headers != null)
			this.headers.putAll(headers);
		return this;
	}

	public void sendRedirect(String location) {
		this.setHeader(HttpHeaders.LOCATION, location);
		this.setStatus(HttpStatus.FOUND);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		if (body == null) {
			this.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(0));
		} else {
			this.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length()));
		}
		this.body = body;
	}

	public int length() {
		return getHttpString().length();
	}
}
