package com._7aske.grain.http.session;

import com._7aske.grain.util.formatter.Formatter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
	private Map<String, Object> cookieData;
	private String name;
	private String value;

	public Cookie() {
		this.cookieData = new HashMap<>();
	}

	public Cookie(String name, String value) {
		this.cookieData = new HashMap<>();
		this.setPath("/");
		this.name = name;
		this.value = value;
	}

	// @Incomplete this should parse all the cookies from Cookie
	// header not treat the whole header as one cookie
	public static Cookie parse(String data) {
		Cookie cookie = new Cookie();
		for (String kv : data.split(";\\s?")) {
			String[] parts = kv.split("\\s*=\\s*");
			String key = parts[0];
			String value = parts[1] == null ? null : URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
			if (Attr.map.get(key) == Attr.EXPIRES) {
				cookie.cookieData.put(Attr.EXPIRES.value, value == null ? 0 : Long.parseLong(value));
			} else if (Attr.map.get(key) == Attr.MAX_AGE) {
				cookie.cookieData.put(Attr.MAX_AGE.value, value == null ? 0 : Long.parseLong(value));
			} else if (Attr.map.get(key) == Attr.DOMAIN) {
				cookie.cookieData.put(Attr.DOMAIN.value, value);
			} else if (Attr.map.get(key) == Attr.PATH) {
				cookie.cookieData.put(Attr.PATH.value, value);
			} else if (Attr.map.get(key) == Attr.SECURE) {
				cookie.cookieData.put(Attr.SECURE.value, true);
			} else if (Attr.map.get(key) == Attr.HTTP_ONLY) {
				cookie.cookieData.put(Attr.HTTP_ONLY.value, true);
			} else if (cookie.name == null) {
				cookie.name = key;
				cookie.value = value;
			} else {
				cookie.cookieData.put(key, value);
			}
		}
		return cookie;
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

	public long getExpires() {
		return (long) cookieData.get(Attr.EXPIRES.value);
	}

	public void setExpires(long expires) {
		this.cookieData.put(Attr.EXPIRES.value, expires);
	}

	public long getMaxAge() {
		return (long) this.cookieData.get(Attr.MAX_AGE.value);
	}

	public void setMaxAge(long maxAge) {
		this.cookieData.put(Attr.MAX_AGE.value, maxAge);
	}

	public String getDomain() {
		return (String) this.cookieData.get(Attr.DOMAIN.value);
	}

	public void setDomain(String domain) {
		cookieData.put(Attr.DOMAIN.value, domain);
	}

	public String getPath() {
		return (String) this.cookieData.get(Attr.PATH.value);
	}

	public void setPath(String path) {
		this.cookieData.put(Attr.PATH.value, path);
	}

	public boolean isHttpOnly() {
		return (boolean) this.cookieData.get(Attr.HTTP_ONLY.value);
	}

	public void setHttpOnly(boolean httpOnly) {
		this.cookieData.put(Attr.HTTP_ONLY.value, httpOnly);
	}

	public boolean isSecure() {
		return (boolean) this.cookieData.get(Attr.SECURE.value);
	}

	public void setSecure(boolean secure) {
		this.cookieData.put(Attr.SECURE.value, secure);
	}

	public Object get(String key) {
		return cookieData.get(key);
	}

	public void put(String key, Object value) {
		cookieData.put(key, value);
	}

	public void remove(String key) {
		cookieData.remove(key);
	}

	public enum Attr {
		EXPIRES("Expires"),
		MAX_AGE("Max-Age"),
		DOMAIN("Domain"),
		PATH("Path"),
		SECURE("Secure"),
		HTTP_ONLY("HttpOnly");

		public static Map<String, Attr> map = new HashMap<>();
		static {
			for (Attr a : values()) {
				map.put(a.value, a);
			}
		}

		private final String value;

		Attr(String value) {
			this.value = value;
		}
	}

	@Override
	public String toString() {

		return this.name + "=" + URLEncoder.encode(this.value, StandardCharsets.UTF_8) + "; " +
				this.cookieData.entrySet()
						.stream()
						.filter(kv -> kv.getValue() != null)
						.map(kv -> new Formatter("{}={}").format(kv.getKey(), URLEncoder.encode((String) kv.getValue(), StandardCharsets.UTF_8)))
						.collect(Collectors.joining("; "));
	}
}
