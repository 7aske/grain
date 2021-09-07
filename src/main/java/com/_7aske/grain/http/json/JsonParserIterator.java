package com._7aske.grain.http.json;

import com._7aske.grain.util.iterator.IndexedStringIterator;

public class JsonParserIterator extends IndexedStringIterator {
	public JsonParserIterator(String content) {
		super(content);
	}


	public String eatKey() {
		StringBuilder builder = new StringBuilder();
		String ch;
		if (peek().equals("\""))
			next();
		while (hasNext() && !(ch = next()).equals("\"")) {
			if (ch.equals("\\")) {
				String peek = peek();
				if (peek.equals("\t") || peek.equals("\n") || peek.equals("\\") || peek.equals("\"")) {
					builder.append(next());
				} else {
					// TODO: handle error
				}
			} else {
				builder.append(ch);
			}
		}
		return builder.toString();
	}
}
