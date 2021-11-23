package com._7aske.grain.util;

import java.util.Map;

public class RequestParams {
	private Map<String, Object> parameters;

	public RequestParams(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getStringParameter(String key) {
		if (this.parameters.get(key) == null) return null;
		return (String) ((Object[]) this.parameters.get(key))[0];
	}

	public String[] getArrayParameter(String key) {
		if (this.parameters.get(key) == null) return null;
		return (String[]) this.parameters.get(key);
	}

	public Object getParameter(String key) {
		return parameters.get(key);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
}
