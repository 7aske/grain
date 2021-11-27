package com._7aske.grain.http;

import com._7aske.grain.http.session.Cookie;
import com._7aske.grain.util.ArrayUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// TODO: provide interface to hide utility methods
public class HttpRequest {
	private String version;
	private String path;
	private HttpMethod method;
	private final Map<String, String> headers;
	private final Map<String, Object> parameters;
	private String queryString;
	private Object body;

	public HttpRequest() {
		this.headers = new HashMap<>();
		this.parameters = new HashMap<>();
	}

	public HttpRequest(HttpRequest other) {
		this.version = other.version;
		this.path = other.path;
		this.method = other.method;
		this.headers = other.headers;
		this.parameters = other.parameters;
		this.queryString = other.queryString;
		this.body = other.body;
	}

	public Cookie getCookie(String name) {
		String cookieData = headers.get(HttpHeaders.COOKIE);
		if (cookieData == null) {
			return null;
		}
		return parseCookie(cookieData).get(name);
	}

	private Map<String, Cookie> parseCookie(String data) {
		return Cookie.parse(data);
	}

	private void setCookie(Cookie cookie) {
		this.headers.put(HttpHeaders.SET_COOKIE, cookie.toString());
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
		String[] parts = path.split("\\?");
		this.path = parts[0];
		if (parts.length == 2) {
			try {
				// TODO: get charset from request?
				this.queryString = URLDecoder.decode(parts[1], StandardCharsets.UTF_8.toString());
				putParameters(this.queryString);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public void putParameters(Map<String, Object> parameters) {
		this.parameters.putAll(parameters);
	}

	public void putParameters(String queryString) {
		String[] parameters = queryString.split("&");
		for (String parameter : parameters) {
			String[] kv = parameter.split("=");
			if (kv.length == 2) {
				String[] values = kv[1].split(",");
				if (this.parameters.containsKey(kv[0])) {
					String[] existing = (String[]) this.parameters.get(kv[0]);
					String[] updated = ArrayUtil.join(existing, values);
					this.parameters.put(kv[0], updated);
				} else {
					this.parameters.put(kv[0], values);
				}
			}
		}
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

	public boolean hasHeader(String header) {
		return headers.get(header) != null;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getStringParameter(String key) {
		if (this.parameters.get(key) == null) return null;
		return (String) ((Object[]) this.parameters.get(key))[0];
	}

	public String[] getArrayParameter(String key) {
		if (this.parameters.get(key) == null) return null;
		return (String[]) this.parameters.get(key);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getQueryString() {
		return queryString;
	}

	@Override
	public String toString() {
		return "HttpRequest{" +
				"version='" + version + '\'' +
				", path='" + path + '\'' +
				", method=" + method +
				", headers=" + headers +
				", parameters=" + parameters +
				", body=" + body +
				'}';
	}

}
