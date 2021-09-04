package com._7aske.grain.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class StringIterator implements Iterator<String> {
	private final String content;
	private final int endIndex;
	private int index = 0;

	public StringIterator(String content) {
		this.content = content;
		this.endIndex = content.length();
	}

	@Override
	public boolean hasNext() {
		return index < endIndex;
	}

	@Override
	public String next() {
		if (hasNext())
			return String.valueOf(content.charAt(index++));
		throw new NoSuchElementException();
	}

	public String peek() {
		if (hasNext())
			return String.valueOf(content.charAt(index));
		throw new NoSuchElementException();
	}

	public boolean isPeek(String val) {
		if (hasNext())
			return String.valueOf(content.charAt(index)).equals(val);
		return false;
	}

	public String eatWhile(Predicate<String> predicate) {
		StringBuilder builder = new StringBuilder();

		while (hasNext() && predicate.test(peek()))
			builder.append(next());

		return builder.toString();
	}

	public String eatWhitespace() {
		return eatWhile(String::isBlank);
	}

	public String eatWord() {
		return eatWhile(ch -> !ch.isBlank());
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
