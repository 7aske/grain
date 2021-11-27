package com._7aske.grain.http.session;

import com._7aske.grain.util.formatter.StringFormat;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * CLass representation of RFC6265 cookie
 */
public class Cookie {
	// @formatter:off
	private String  name     = null;
	private String  value    = null;
	@Deprecated // only version 0 uses expires
	private long    expires  = -1;
	private long    maxAge   = -1;
	private String  domain   = null;
	private String  path     = null;
	private boolean secure   = false;
	private boolean httpOnly = false;
	// @formatter:on

	public static final long DEFAULT_EXPIRY_VALUE = 0L;

	private Cookie() {
	}

	public Cookie(String name, String value, Long expires, Long maxAge, String domain, String path, boolean secure, boolean httpOnly) {
		this.name = name;
		this.value = value;
		this.expires = expires;
		this.maxAge = maxAge;
		this.domain = domain;
		this.path = path;
		this.secure = secure;
		this.httpOnly = httpOnly;
	}

	public Cookie(String name, String value) {
		this.setPath("/");
		this.name = name;
		this.value = value;
	}

	// @Refactor this can be made much more concise
	public static Map<String, Cookie> parse(String data) {
		Map<String, Cookie> cookies = new HashMap<>();
		Cookie cookie = new Cookie();
		for (String kv : data.split(";\\s?")) {
			String[] parts = kv.split("\\s*=\\s*");
			String key = parts[0];
			String value = parts[1] == null ? null : URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
			if (Attr.get(key) == Attr.EXPIRES) {
				cookie.expires = value == null ? 0 : Long.parseLong(value);
			} else if (Attr.get(key) == Attr.MAX_AGE) {
				cookie.maxAge = value == null ? 0 : Long.parseLong(value);
			} else if (Attr.get(key) == Attr.DOMAIN) {
				cookie.domain = value;
			} else if (Attr.get(key) == Attr.PATH) {
				cookie.path = value;
			} else if (Attr.get(key) == Attr.SECURE) {
				cookie.secure = true;
			} else if (Attr.get(key) == Attr.HTTP_ONLY) {
				cookie.httpOnly = true;
			} else if (cookie.name == null) {
				// If none of the attributes match that means that
				// we have the cookie name=value pair
				cookie.name = key;
				cookie.value = value;
			} else {
				// If the cookie name is already been set and none of
				// the attributes match it means that we've come across
				// a new cookie, and therefore we save it and reset the
				// cookie name and value
				cookies.put(cookie.name, cookie);
				cookie = new Cookie();
				cookie.name = key;
				cookie.value = value;
			}
		}
		if (cookie.name != null) {
			cookies.put(cookie.name, cookie);
		}
		return cookies;
	}

	public boolean isExpired() {
		if (maxAge <= 0) return true;
		// Max-Age is in seconds
		return (maxAge < System.currentTimeMillis() / 1000);
	}

	public void setExpired() {
		maxAge = -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public enum Attr {
		EXPIRES("expires"),
		MAX_AGE("max-age"),
		DOMAIN("domain"),
		PATH("path"),
		SECURE("secure"),
		HTTP_ONLY("httpOnly");

		public static Map<String, Attr> map = new HashMap<>();
		static {
			for (Attr a : values()) {
				map.put(a.value, a);
			}
		}

		public static Attr get(String key) {
			return map.get(key.toLowerCase(Locale.ROOT));
		}

		private final String value;

		Attr(String value) {
			this.value = value;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(StringFormat.format("{}={}; ", name, value));
		if (maxAge > DEFAULT_EXPIRY_VALUE)
			builder.append(StringFormat.format("{}={}; ", Attr.MAX_AGE.value, maxAge));
		if (expires > DEFAULT_EXPIRY_VALUE)
			builder.append(StringFormat.format("{}={}; ", Attr.EXPIRES.value, expires));
		if (path != null)
			builder.append(StringFormat.format("{}={}; ", Attr.PATH.value, path));
		if (domain != null)
			builder.append(StringFormat.format("{}={}; ", Attr.DOMAIN.value, path));
		if (secure)
			builder.append(StringFormat.format("{}; ", Attr.SECURE.value));
		if (httpOnly)
			builder.append(StringFormat.format("{}; ", Attr.HTTP_ONLY.value));
		return builder.toString();
	}
}
