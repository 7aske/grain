package com._7aske.grain.gtl.ast.types;

import com._7aske.grain.gtl.lexer.TokenType;

public enum AstRelationalOperator {
	GT(700),
	LT(700),
	GE(700),
	LE(700);

	private final int precedence;

	AstRelationalOperator(int precedence) {
		this.precedence = precedence;
	}

	public int getPrecedence() {
		return precedence;
	}

	public static AstRelationalOperator from(TokenType type) {
		switch (type) {
			case GT:
				return GT;
			case LT:
				return LT;
			case GE:
				return GE;
			case LE:
				return LE;
			default:
				throw new RuntimeException("Invalid type " + type);
		}
	}
}
