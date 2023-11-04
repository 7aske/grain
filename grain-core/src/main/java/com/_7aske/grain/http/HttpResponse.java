package com._7aske.grain.http;

import com._7aske.grain.http.session.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com._7aske.grain.http.HttpConstants.HTTP_V1;

public class HttpResponse {
	private final Map<String, String> headers;
	private String version = HTTP_V1;
	private HttpStatus status;
	private final Map<String, Cookie> cookies;
	private final ByteArrayOutputStream outputStream;

	public HttpResponse() {
		this(HttpStatus.OK);
	}

	public HttpResponse(HttpStatus status) {
		this.status = status;
		this.headers = new HashMap<>();
		this.cookies = new HashMap<>();
		this.outputStream = new ByteArrayOutputStream();
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


	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	ByteArrayOutputStream getByteArrayOutputStream() {
		return this.outputStream;
	}

	Map<String, Cookie> getCookies() {
		return cookies;
	}

	public int length() {
		return this.getByteArrayOutputStream().size();
	}

	public boolean isEmpty() {
		return this.length() == 0;
	}
}
