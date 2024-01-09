package com._7aske.grain.web.http;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.http.session.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import static com._7aske.grain.web.http.HttpConstants.HTTP_V1;

public class GrainHttpResponse implements HttpResponse {
	private final Map<String, List<String>> headers;
	private String version = HTTP_V1;
	private HttpStatus status;
	private final Map<String, Cookie> cookies;
	private final ByteArrayOutputStream outputStream;
	private final PrintWriter writer;
	private boolean usingOutputStream = false;
	private boolean usingWriter = false;
	private boolean committed = false;

	public GrainHttpResponse() {
		this(HttpStatus.OK);
	}

	public GrainHttpResponse(HttpStatus status) {
		this.status = status;
		this.headers = new HashMap<>();
		this.cookies = new HashMap<>();
		this.outputStream = new ByteArrayOutputStream();
		this.writer = new PrintWriter(this.outputStream);
	}

	public void setCookie(Cookie cookie) {
		this.cookies.put(cookie.getName(), cookie);
	}

	public Cookie getCookie(String name) {
		return cookies.get(name);
	}

	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	public String getVersion() {
		return version;
	}

	public GrainHttpResponse setVersion(String version) {
		this.version = version;
		return this;
	}

	@Override
	public int getStatus() {
		return status.getValue();
	}

	@Override
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public String getHeader(String header) {
		List<String> values = headers.get(header);
		if (values == null || values.isEmpty()) return null;

		return values.get(0);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	public void setHeader(String header, String value) {
		List<String> values = new ArrayList<>(1);
		values.add(value);
		headers.put(header, values);
	}

	@Override
	public void addHeader(String name, String value) {
        List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>(1));
        values.add(value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value));

	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	@Override
	public void setStatus(int sc) {
		this.status = HttpStatus.valueOf(sc);
	}

	public void removeHeader(String header) {
		headers.remove(header);
	}

	@Override
	public void sendRedirect(String location) {
		this.setHeader(HttpHeaders.LOCATION, location);
		this.setStatus(HttpStatus.FOUND);
	}

	@Override
	public void setDateHeader(String name, long date) {
		setHeader(name, String.valueOf(date));
	}

	@Override
	public void addDateHeader(String name, long date) {
		addHeader(name, String.valueOf(date));
	}


	@Override
	public String getCharacterEncoding() {
		return getHeader(HttpHeaders.ACCEPT_ENCODING);
	}

	@Override
	public String getContentType() {
		return getHeader(HttpHeaders.CONTENT_TYPE);
	}

	@Override
	public OutputStream getOutputStream() {
		usingOutputStream = true;
		if (usingWriter) {
			throw new IllegalStateException("getWriter() has already been called for this response");
		}

		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		usingWriter = true;
		if (usingOutputStream) {
			throw new IllegalStateException("getOutputStream() has already been called for this response");
		}

		return writer;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		setHeader(HttpHeaders.ACCEPT_ENCODING, charset);
	}

	@Override
	public void setContentLength(int len) {
		setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(len));
	}

	@Override
	public void setContentLengthLong(long length) {
		setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
	}

	@Override
	public void setContentType(String type) {
		setHeader(HttpHeaders.CONTENT_TYPE, type);
	}

	@Override
	public void setBufferSize(int size) {
		throw new GrainRuntimeException("HttpResponse#setBufferSize - Not implemented");
	}

	@Override
	public int getBufferSize() {
		throw new GrainRuntimeException("HttpResponse#getBufferSize - Not implemented");
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new GrainRuntimeException("HttpResponse#flushBuffer - Not implemented");
	}

	@Override
	public void resetBuffer() {
		throw new GrainRuntimeException("HttpResponse#resetBuffer - Not implemented");
	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}

	@Override
	public void reset() {
		this.headers.clear();
		this.cookies.clear();
		this.outputStream.reset();
	}

	@Override
	public void setLocale(Locale loc) {
		setHeader(HttpHeaders.ACCEPT_LANGUAGE, loc.getLanguage());
	}

	@Override
	public Locale getLocale() {
		return Optional.ofNullable(getHeader(HttpHeaders.ACCEPT_LANGUAGE))
				.map(Locale::new)
				.orElse(Locale.getDefault());
	}

	@Override
	public void addCookie(Cookie cookie) {
		this.setCookie(cookie);

	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		setStatus(sc);
		setHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN);
		getOutputStream().write(msg.getBytes());
		getOutputStream().close();
	}

	@Override
	public void sendError(int sc) throws IOException {
		setStatus(sc);
		getOutputStream().close();
	}

	public void writeTo(OutputStream outputStream) throws IOException {
		this.outputStream.writeTo(outputStream);
		outputStream.flush();
	}

	@Override
	public String toString() {
		return "GrainHttpResponse{" +
			   "version='" + version + '\'' +
			   ", status=" + status +
			   '}';
	}
}
