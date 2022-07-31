package com._7aske.grain.web.view;

import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.HashMap;
import java.util.Map;

public class TemplateView extends FileView {
	private Map<String, Object> data = null;
	private String cachedContent = null;

	public TemplateView(String path) {
		super(path);
	}

	public void setData(String key, Object value) {
		if (data == null)
			data = new HashMap<>();
		data.put(key, value);
	}

	@Override
	public String getContent() {
		if (cachedContent == null) {

			cachedContent = Interpreter.interpret(super.getContent(), data);
		}

		return cachedContent;
	}
}
