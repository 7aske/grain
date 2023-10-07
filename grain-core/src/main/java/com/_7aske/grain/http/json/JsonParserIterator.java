package com._7aske.grain.http.json;

import com._7aske.grain.util.iterator.IndexedCodepointIterator;

public class JsonParserIterator extends IndexedCodepointIterator {
	public JsonParserIterator(String content) {
		super(content);
	}


	public String eatKey() {
		StringBuilder builder = new StringBuilder();
		int ch;

		if (peek() == '"')
			next();

		while (hasNext() && ((ch = next()) != '"')) {
			if (ch == '\\') {
				int peek = peek();
				if (peek == '\t' || peek == '\n' || peek == '\\' || peek == '"') {
					builder.append(next());
				} else {
					// TODO: handle error
				}
			} else {
				builder.appendCodePoint(ch);
			}
		}
		return builder.toString();
	}
}
