package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.util.iterator.IndexedCodepointIterator;

public class JsonParserIterator extends IndexedCodepointIterator {
	public JsonParserIterator(String content) {
		super(content);
	}


	public String eatString() {
		StringBuilder builder = new StringBuilder();
		int ch;

		if (peek() == '"')
			next();

		if (!hasNext()) {
			throw new JsonDeserializationException("Unexpected end of input " + getInfo());
		}

		while (hasNext() && ((ch = next()) != '"')) {
			if (ch == 0) {
				throw new JsonDeserializationException("Unexpected end of input " + getInfo());
			}

			if (ch == 10) {
				throw new JsonDeserializationException("Unexpected end of line " + getInfo());
			}

			if (ch == 9) {
				throw new JsonDeserializationException("Unexpected tab " + getInfo());
			}

			if (ch == '\\') {

				int peek = peek();
				if (peek == 'u') {
					builder.appendCodePoint(ch);
					builder.appendCodePoint(next());
					for (int i = 0; i < 4; i++) {
						int next = next();
						if ((next < '0' || next > '9') && (next < 'A' || next > 'F') && (next < 'a' || next > 'f')) {
							throw new JsonDeserializationException("Invalid unicode escape sequence " + getInfo());
						}
						builder.appendCodePoint(next);
					}
				} else if (peek == 't' || peek == 'n' || peek == '\\' || peek == '"' || peek == 'r' || peek == 'b' || peek == 'f' || peek == '/') {
					builder.appendCodePoint(ch);
					builder.appendCodePoint(next());
				} else {
					throw new JsonDeserializationException("Invalid escape sequence " + getInfo());
				}
			} else {
				builder.appendCodePoint(ch);
			}
		}
		return builder.toString();
	}
}
