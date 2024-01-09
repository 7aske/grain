package com._7aske.grain.web.http;

import com._7aske.grain.util.ArrayUtil;
import com._7aske.grain.util.StringUtils;
import com._7aske.grain.web.http.multipart.Part;
import com._7aske.grain.web.http.session.Cookie;
import com._7aske.grain.web.http.session.Session;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

import static com._7aske.grain.web.http.HttpConstants.BOUNDARY_SEP;

public class GrainHttpRequest implements HttpRequest {
	private static final int BUFFER_SIZE = 8 * 1024;
	private String version;
	private String path;
	private HttpMethod method;
	private final Map<String, String> headers;
	private final Map<String, String[]> parameters;
	private final Map<String, Cookie> cookies;
	private String queryString;
	private Session session;
	private ByteArrayOutputStream outputStream;
	private InputStream inputStream;
	private BufferedReader reader;
	private boolean usingReader = false;
	private boolean usingOutputStream = false;
	private String scheme;
	private List<Locale> locales = List.of(Locale.getDefault());
	private final String requestId;
	private final Map<String, Object> attributes;
	private String remoteAddr;
	private String remoteHost;
	private int remotePort;
	private String localName;
	private String localAddr;
	private int localPort;
	private String boundary;
	private final List<Part> parts;

	public GrainHttpRequest() {
		this.headers = new HashMap<>();
		this.parameters = new HashMap<>();
		this.cookies = new HashMap<>();
		this.attributes = new HashMap<>();
		this.parts = new ArrayList<>(0);
		this.requestId = UUID.randomUUID().toString();
	}

	public void addCookie(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
	}

	public Cookie getCookie(String name) {
		if (cookies.isEmpty()) {
			String cookieData = getHeader(HttpHeaders.COOKIE);
			this.cookies.putAll(Cookie.parse(cookieData));
		}
		return cookies.get(name);
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
		if (path.contains("?")) {
			throw new IllegalArgumentException("Path cannot contain query string");
		}
		this.path = path;
	}

	public void putParameters(Map<String, String[]> parameters) {
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			this.parameters.merge(entry.getKey(), entry.getValue(), ArrayUtil::join);
		}
	}

	@Override
	public String getCharacterEncoding() {
		return Optional.ofNullable(getHeader(HttpHeaders.CONTENT_ENCODING))
				.orElse(StandardCharsets.ISO_8859_1.name());
	}

	@Override
	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		try {
			headers.put(HttpHeaders.CONTENT_ENCODING, Charset.forName(encoding).name());
		} catch (UnsupportedCharsetException ex) {
			throw new UnsupportedEncodingException("Unsupported encoding: " + encoding);
		}
	}

	@Override
	public long getContentLength() {
		String contentLength = getHeader(HttpHeaders.CONTENT_LENGTH);
		if (contentLength != null) {
			return Long.parseLong(contentLength);
		}

		return -1;
	}

	@Override
	public String getContentType() {
		return getHeader(HttpHeaders.CONTENT_TYPE);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		usingOutputStream = true;
		if (usingReader) {
			throw new IllegalStateException("Cannot use input stream after reader has been used");
		}
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		}

		return inputStream;
	}

	public OutputStream getOutputStream() {
		if (outputStream == null) {
			outputStream = new ByteArrayOutputStream(BUFFER_SIZE);
		}

		return outputStream;
	}

	@Override
	public String getParameter(String name) {
		return parameters.getOrDefault(name, new String[]{null})[0];
	}

	@Override
	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	@Override
	public String[] getParameterValues(String name) {
		return parameters.get(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return parameters;
	}

	@Override
	public String getProtocol() {
		return getVersion();
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String getServerName() {
		String host = getHeader(HttpHeaders.HOST);
        if (host == null) {
            return null;
        }

        String[] hostParts = host.split(":");
        return hostParts[0];
    }

	@Override
	public int getServerPort() {
		String host = getHeader(HttpHeaders.HOST);
		if (host == null) {
			return -1;
		}

		String[] hostParts = host.split(":");
		if (hostParts.length == 2) {
			return Integer.parseInt(hostParts[1]);
		}

		return 80;
	}

	@Override
	public BufferedReader getReader() {
		usingReader = true;
		if (usingOutputStream) {
			throw new IllegalStateException("Cannot use reader after output stream has been used");
		}

		if (reader == null) {
			reader = new BufferedReader(new InputStreamReader(inputStream));
		}

		return reader;
	}

	@Override
	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	@Override
	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	@Override
	public Locale getLocale() {
		try {
			return locales.get(0);
		} catch (IndexOutOfBoundsException ignored) {
			locales = List.of(Locale.getDefault());
			return locales.get(0);
		}
	}

	@Override
	public List<Locale> getLocales() {
		return locales;
	}


	@Override
	public boolean isSecure() {
		return StringUtils.equalsIgnoreCase(scheme, "https");
	}

	@Override
	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	@Override
	public String getLocalAddr() {
		return localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	@Override
	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	public Cookie[] getCookies() {
		Cookie[] cookieArray = new Cookie[this.cookies.size()];
		int i = 0;
		for (Map.Entry<String, Cookie> entry : this.cookies.entrySet()) {
			cookieArray[i++] = entry.getValue();
		}
		return cookieArray;
	}

	@Override
	public long getDateHeader(String name) {
		try {
			return Long.parseLong(getHeader(name));
		} catch (NumberFormatException ignored) {
			return -1;
		}
	}

	@Override
	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	@Override
	public String getHeader(String header) {
		return headers.get(header);
	}

	@Override
	public Set<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public int getIntHeader(String name) {
		return Integer.parseInt(getHeader(name));
	}

	public void setHeader(String header, String value) {
		headers.put(header, value);
	}

	public boolean hasHeader(String header) {
		return headers.get(header) != null;
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String getRequestURI() {
		return getPath();
	}

	@Override
	public StringBuffer getRequestURL() {
		StringBuffer buffer = new StringBuffer();
		return buffer.append(scheme)
				.append("://")
				.append(getServerName())
				.append(":")
				.append(getServerPort())
				.append(getRequestURI());
	}

	@Override
	public Session getSession(boolean create) {
		// TODO
		return null;
	}

	@Override
	public Session getSession() {
		// TODO
		return session;
	}

	@Override
	public List<Part> getParts() {
		return parts;
	}

	public void addPart(Part part) {
		this.parts.add(part);
	}

	@Override
	public Part getPart(String name) {
		return parts.stream()
				.filter(part -> part.getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}
	
	public String getBoundary() {
		return BOUNDARY_SEP + boundary;
	}
	
	public void setBoundary(String paramPart) {
		boundary = paramPart;
	}

	@Override
	public String toString() {
		return "GrainHttpRequest{" +
			   "version='" + version + '\'' +
			   ", path='" + path + '\'' +
			   ", method=" + method +
			   '}';
	}
}
