package com._7aske.grain.http.view;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataView extends AbstractView {
	private final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([A-Za-z-_$]+)}}");
	private Map<String, String> data = null;

	public DataView(String path) {
		super(path);
	}

	public void setData(String key, String value) {
		if (data == null)
			data = new HashMap<>();
		data.put(key, value);
	}

	@Override
	public String getContent() {
		String content = super.getContent();
		if (data == null)
			return content;

		StringBuilder builder = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(content);

		while (matcher.find()) {
			String key = matcher.group(1);
			if (key != null)
				matcher.appendReplacement(builder, data.get(key));
		}
		matcher.appendTail(builder);

		return builder.toString();
	}
}
