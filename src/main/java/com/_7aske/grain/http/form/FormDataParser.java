package com._7aske.grain.http.form;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class FormDataParser {
	private final String content;
	public FormDataParser(String content) {
		this.content = content;
	}

	public Map<String, String> parse() {
		Map<String, String> result = new HashMap<>();

		for (String kv : content.split("&")) {
			if (kv.isEmpty() || kv.isBlank()) continue;
			String[] kvParams = kv.split("=");

			if (kvParams.length != 2) continue;

			String key = kvParams[0];
			String val = URLDecoder.decode(kvParams[1], StandardCharsets.UTF_8);

			result.put(key, val);
		}

		return result;
	}
}
