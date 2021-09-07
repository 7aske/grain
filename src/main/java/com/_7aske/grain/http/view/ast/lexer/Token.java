package com._7aske.grain.http.view.ast.lexer;

import java.util.Optional;

public class Token {
	private TokenType type;
	private String value;
	private final int[] range;
	private int row;

	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
		this.range = new int[2];
		this.row = -1;
	}

	public static Optional<Token> optional(TokenType type, String value) {
		return Optional.of(new Token(type, value));
	}

	public static Optional<Token> empty() {
		return Optional.empty();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public void setStartChar(int val) {
		range[0] = val;
		setEndChar(val + value.length());
	}


	public void setEndChar(int val) {
		range[1] = val;
	}
	public void setRow(int val) {
		row = val;
	}

	public int getStartChar() {
		return range[0];
	}

	public int getEndChar() {
		return range[1];
	}

	public int getRow() {
		return row;
	}


	public String getInfo() {
		return String.format("at character %d, row %d", range[0], row);
	}

	@Override
	public String toString() {
		return String.format("%s('%s')@%d:%d-%d", type, value, row, range[0], range[1]);
	}
}
