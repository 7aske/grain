package com._7aske.grain.http.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.LexerException;
import com._7aske.grain.util.formatter.StringFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import static com._7aske.grain.compiler.interpreter.Interpreter.*;

public class DataView extends FileView {
	private Map<String, Object> data = null;
	private String cachedContent = null;

	public DataView(String path) {
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
