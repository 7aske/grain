package com._7aske.grain.web.http;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.util.ArrayUtil;
import com._7aske.grain.util.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility wrapper for manipulating request parameters parsed from an HTTP request.
 */
public class RequestParams {
	public static final Pattern URL_ENCODED_VALUE_LIST_SEPARATOR_REGEX = Pattern.compile("\\s*,\\s*");
	public static final Pattern URL_ENCODED_VALUE_SEPARATOR = Pattern.compile("=");
	public static final Pattern URL_ENCODED_KEY_SEPARATOR = Pattern.compile("&");

	private final Map<String, String[]> parameters;

	public RequestParams(@NotNull Map<String, String[]> parameters) {
		this.parameters = parameters;
	}

	public static RequestParams parse(String queryString, String characterEncoding) {
		if (StringUtils.isBlank(queryString)) {
			return new RequestParams(Map.of());
		}

		String actualCharacterEncoding = Optional.ofNullable(characterEncoding)
				.orElse(StandardCharsets.ISO_8859_1.name());

		Map<String, String[]> params = new HashMap<>();

		Arrays.stream(URL_ENCODED_KEY_SEPARATOR.split(queryString))
				.map(URL_ENCODED_VALUE_SEPARATOR::split)
				.forEach(kv -> {
					if (kv.length == 2) {
						String[] values = URL_ENCODED_VALUE_LIST_SEPARATOR_REGEX
								.split(URLDecoder.decode(kv[1], Charset.forName(actualCharacterEncoding)));
						if (params.containsKey(kv[0])) {
							String[] existing = params.get(kv[0]);
							String[] updated = ArrayUtil.join(existing, values);
							params.put(kv[0], updated);
						} else {
							params.put(kv[0], values);
						}
					} else {
						params.put(kv[0], new String[]{""});
					}
				});

		return new RequestParams(params);
	}

	public @NotNull String[] getArrayParameter(@NotNull String key) {
		if (this.parameters.get(key) == null) return new String[]{};
		return this.parameters.get(key);
	}

	public @Nullable String getParameter(String key) {
		String[] values = this.parameters.get(key);

		if (ArrayUtil.isEmpty(values)) {
			return null;
		}

		return this.parameters.get(key)[0];
	}

	public @NotNull Map<String, String[]> getParameters() {
		return parameters;
	}
}
