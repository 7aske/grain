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

		if (peek() == '"') {
			next();
		}

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
					// Unicode escape sequences are 4 characters long. If something
					// bad happens during parsing of the unicode escape sequence,
					// we throw.
					for (int i = 0; i < 4; i++) {
						int next = next();
						if ((next < '0' || next > '9') && (next < 'A' || next > 'F') && (next < 'a' || next > 'f')) {
							throw new JsonDeserializationException("Invalid unicode escape sequence " + getInfo());
						}
						builder.appendCodePoint(next);
					}
				} else if (isValidEscapeCharacter(peek)) {
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

	private boolean isValidEscapeCharacter(int ch) {
		return  ch == 't'  ||
				ch == 'n'  ||
				ch == 'r'  ||
				ch == 'b'  ||
				ch == 'f'  ||
				ch == '\\' ||
				ch == '"'  ||
				ch == '/';
	}
}
