package com._7aske.grain.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

public class CodepointIterator implements Iterator<Integer> {
	protected final String content;
	protected final int endIndex;
	protected int index = 0;

	public CodepointIterator(String content) {
		this.content = content;
		this.endIndex = content.length();
	}

	@Override
	public boolean hasNext() {
		return index < endIndex;
	}

	@Override
	public Integer next() {
		if (hasNext())
			return content.codePointAt(index++);
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

	public Integer prev() {
		if (index > 0)
			return content.codePointAt(index - 1);
		throw new NoSuchElementException();
	}

	public Integer peek() {
		if (hasNext())
			return content.codePointAt(index);
		throw new NoSuchElementException();
	}

	public boolean isPeek(int val, int... vals) {
		if (!hasNext()) {
			return false;
		}

		int curr = content.codePointAt(index);

		if (curr == val) {
			return true;
		}

		for (int v : vals) {
			if (curr == v) {
				return true;
			}
		}

		return false;
	}

	public int getIndex() {
		return index;
	}

	public String eatWhile(IntPredicate predicate) {
		StringBuilder builder = new StringBuilder();

		while (hasNext() && predicate.test(peek()))
			builder.appendCodePoint(next());

		return builder.toString();
	}

	public String eatWhitespace() {
		return eatWhile(ch -> Character.isSpaceChar(ch) || ch == '\t' || ch == '\n');
	}

	public String eatWord() {
		return eatWhile(ch -> Character.isDigit(ch) || Character.isAlphabetic(ch));
	}
}
