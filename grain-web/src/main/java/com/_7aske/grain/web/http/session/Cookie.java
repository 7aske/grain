package com._7aske.grain.web.http.session;

import com._7aske.grain.web.exception.CookieParsingException;
import com._7aske.grain.util.formatter.StringFormat;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * CLass representation of RFC6265 cookie
 */
public class Cookie implements SessionToken {
	public static final String DOMAIN = "Domain";
	public static final String MAX_AGE = "Max-Age";
	public static final String PATH = "Path";
	public static final String SECURE = "Secure";
	public static final String HTTP_ONLY = "HttpOnly";

	private String  name     = null;
	private String  value    = null;
	private volatile ConcurrentMap<String,String> attributes;

	public static final long DEFAULT_EXPIRY_VALUE = 0L;

	private Cookie() {
	}

	public Cookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	// @Refactor this can be made much more concise
	public static Map<String, Cookie> parse(String data) {
		Map<String, Cookie> cookies = new HashMap<>();
		if (data == null)
			return cookies;
		Cookie cookie = new Cookie();
		for (String kv : data.split(";\\s?")) {
			String[] parts = kv.split("\\s*=\\s*");
			if (parts.length == 0) {
				continue;
			}

            for (String part : parts) {
                if (part == null) {
                    throw new CookieParsingException("Cookie parsing failed: " + data);
                }
            }

			String key = parts[0].toLowerCase();
			String value = parts[1] == null ? null : URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
			if (key.equals(MAX_AGE.toLowerCase())) {
				cookie.setAttributeInternal(MAX_AGE, value);
			} else if (key.equals(DOMAIN.toLowerCase())) {
				cookie.setAttributeInternal(DOMAIN, value);
			} else if (key.equals(PATH.toLowerCase())) {
				cookie.setAttributeInternal(PATH, value);
			} else if (key.equals(SECURE.toLowerCase())) {
				cookie.setAttributeInternal(SECURE, value);
			} else if (key.equals(HTTP_ONLY.toLowerCase())) {
				cookie.setAttributeInternal(HTTP_ONLY, value);
			} else if (cookie.name == null) {
				// If none of the attributes match that means that
				// we have the cookie name=value pair
				cookie.name = parts[0];
				cookie.value = value;
			} else {
				// If the cookie name is already been set and none of
				// the attributes match it means that we've come across
				// a new cookie, and therefore we save it and reset the
				// cookie name and value
				cookies.put(cookie.name, cookie);
				cookie = new Cookie();
				cookie.name = parts[0];
				cookie.value = value;
			}
		}

		if (cookie.name != null) {
			cookies.put(cookie.name, cookie);
		}

		return cookies;
	}

	public boolean isExpired() {
		int maxAge = getMaxAge();
		// -1 is the session cookie
		if (maxAge == -1) return false;
		// Max-Age is in seconds
		return (maxAge < System.currentTimeMillis() / 1000);
	}

	public void setDomain(String pattern) {
		if (pattern == null) {
			setAttributeInternal(DOMAIN, null);
		} else {
			// IE requires the domain to be lower case (unconfirmed)
			setAttributeInternal(DOMAIN, pattern.toLowerCase(Locale.ENGLISH));
		}
	}

	public String getDomain() {
		return getAttribute(DOMAIN);
	}


	public void setMaxAge(int expiry) {
		setAttributeInternal(MAX_AGE, Integer.toString(expiry));
	}

	public int getMaxAge() {
		String maxAge = getAttribute(MAX_AGE);
		if (maxAge == null) {
			return -1;
		} else {
			return Integer.parseInt(maxAge);
		}
	}

	public void setPath(String uri) {
		setAttributeInternal(PATH, uri);
	}

	public String getPath() {
		return getAttribute(PATH);
	}

	public void setSecure(boolean flag) {
		setAttributeInternal(SECURE, Boolean.toString(flag));
	}

	public boolean isSecure() {
		return Boolean.parseBoolean(getAttribute(SECURE));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String newValue) {
		value = newValue;
	}

	public String getValue() {
		return value;
	}

	public void setHttpOnly(boolean httpOnly) {
		setAttributeInternal(HTTP_ONLY, Boolean.toString(httpOnly));
	}

	public boolean isHttpOnly() {
		return Boolean.parseBoolean(getAttribute(HTTP_ONLY));
	}

	@Override
	public String getId() {
		return getValue();
	}

	public void setAttribute(String name, String value) {
		if (name == null) {
			throw new IllegalArgumentException("Cookie attribute name cannot be null");
		}

		if (name.equalsIgnoreCase(MAX_AGE)) {
			if (value == null) {
				setAttributeInternal(MAX_AGE, null);
			} else {
				// Integer.parseInt throws NFE if required
				setMaxAge(Integer.parseInt(value));
			}
		} else {
			setAttributeInternal(name, value);
		}
	}


	private void setAttributeInternal(String name, String value) {
		if (attributes == null) {
			if (value == null) {
				return;
			} else {
				attributes = new ConcurrentHashMap<>();
			}
		}

		attributes.put(name, value);
	}


	public String getAttribute(String name) {
		if (attributes == null) {
			return null;
		} else {
			return attributes.get(name);
		}
	}


	public Map<String,String> getAttributes() {
		if (attributes == null) {
			return Collections.emptyMap();
		} else {
			return Collections.unmodifiableMap(attributes);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(StringFormat.format("{}={}; ", name, value));

		if (attributes == null) {
			return builder.toString();
		}

		attributes.forEach((key, val) -> {
            if (key.equals(SECURE) || key.equals(HTTP_ONLY)) {
                builder.append(StringFormat.format("{}; ", key));
            } else {
                builder.append(StringFormat.format("{}={}; ", key, val));
            }
        });

		return builder.toString();
	}
}
