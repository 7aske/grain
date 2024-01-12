package com._7aske.grain.web.http;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;

import java.util.Map;

/**
 * Utility wrapper for manipulating request parameters parsed from an HTTP request.
 */
public class RequestParams {
	private final Map<String, String[]> parameters;

	public RequestParams(@NotNull Map<String, String[]> parameters) {
		this.parameters = parameters;
	}

	public @NotNull String getStringParameter(@NotNull String key) {
		if (this.parameters.get(key) == null) return "";
		return this.parameters.get(key)[0];
	}

	public @NotNull String[] getArrayParameter(@NotNull String key) {
		if (this.parameters.get(key) == null) return new String[]{};
		return this.parameters.get(key);
	}

	public @Nullable Object getParameter(String key) {
		return parameters.get(key);
	}

	public @NotNull Map<String, String[]> getParameters() {
		return parameters;
	}
}
