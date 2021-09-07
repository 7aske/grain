package com._7aske.grain.http.view.ast.parser;

import com._7aske.grain.http.view.ast.lexer.Token;
import com._7aske.grain.http.view.ast.lexer.TokenType;

import java.util.*;
import java.util.function.Predicate;

public class TokenIterator implements Iterator<Token> {
	protected final List<Token> tokens;
	protected final int endIndex;
	protected int index = 0;

	public TokenIterator(List<Token> tokens) {
		this.tokens = new ArrayList<>(tokens);
		this.endIndex = this.tokens.size();
	}

	@Override
	public boolean hasNext() {
		return index < endIndex;
	}

	@Override
	public Token next() {
		if (hasNext())
			return this.tokens.get(index++);
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

	public Token prev() {
		if (index > 0)
			return tokens.get(index - 1);
		throw new NoSuchElementException();
	}

	public Token peek() {
		if (hasNext())
			return tokens.get(index);
		throw new NoSuchElementException();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<Token> eatWhile(Predicate<Token> predicate) {
		List<Token> eaten = new ArrayList<>();

		while (hasNext() && predicate.test(peek()))
			tokens.add(next());

		return eaten;
	}

	public boolean isPeekOfType(TokenType... typ) {
		return hasNext() && Arrays.asList(typ).contains(peek().getType());
	}
}
