package com._7aske.grain.util.formatter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FormatIterator implements Iterator<Character> {
	protected final char[] content;
	protected final int endIndex;
	protected int index = 0;

	public FormatIterator(String content) {
		this.content = content.toCharArray();
		this.endIndex = this.content.length;
	}

	@Override
	public boolean hasNext() {
		return index < endIndex;
	}

	@Override
	public Character next() {
		if (hasNext())
			return content[index++];
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

	public Character prev() {
		if (index > 0)
			return content[index-1];
		throw new NoSuchElementException();
	}

	public Character peek() {
		if (hasNext())
			return content[index];
		throw new NoSuchElementException();
	}

	public boolean isPeek(Character val) {
		if (hasNext())
			return content[index] == val;
		return false;
	}

	public int getIndex() {
		return index;
	}

	public String eatWhile(Predicate<Character> predicate) {
		StringBuilder builder = new StringBuilder();

		while (hasNext() && predicate.test(peek()))
			builder.append(next());

		return builder.toString();
	}

	public String eatWhitespace() {
		return eatWhile(Character::isWhitespace);
	}
}
