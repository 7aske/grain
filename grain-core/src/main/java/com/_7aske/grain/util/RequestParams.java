package com._7aske.grain.util;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;

import java.util.Map;

/**
 * Utility wrapper for manipulating request parameters parsed from an HTTP request.
 */
public class RequestParams {
	private final Map<String, Object> parameters;

	public RequestParams(@NotNull Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public @NotNull String getStringParameter(@NotNull String key) {
		if (this.parameters.get(key) == null) return "";
		return (String) ((Object[]) this.parameters.get(key))[0];
	}

	public @NotNull String[] getArrayParameter(@NotNull String key) {
		if (this.parameters.get(key) == null) return new String[]{""};
		return (String[]) this.parameters.get(key);
	}

	public @Nullable Object getParameter(String key) {
		return parameters.get(key);
	}

	public @NotNull Map<String, Object> getParameters() {
		return parameters;
	}
}
