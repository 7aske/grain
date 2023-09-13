package com._7aske.grain.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class StringIterator implements Iterator<String> {
	protected final String content;
	protected final int endIndex;
	protected int index = 0;

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

	public void rewind() {
		rewind(1);
	}

	public void rewind(int num) {
		if (this.index - num < 0)
			throw new IndexOutOfBoundsException();

		this.index -= num;
	}

	public String prev() {
		if (index > 0)
			return String.valueOf(content.charAt(index-1));
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

	public int getIndex() {
		return index;
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
		return eatWhile(ch -> ch.matches("[a-zA-Z0-9]+"));
	}

	public String eatFloat() {
		StringBuilder builder = new StringBuilder();
		do {
			if (hasNext())
				builder.append(next());
			else
				return builder.toString();
		} while(builder.toString().matches("^([+-]?\\d+\\.?\\d*)$"));
		rewind();
		builder.setLength(builder.length()-1);
		return builder.toString();
	}

	public String getContent() {
		return content;
	}
}
