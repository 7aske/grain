package com._7aske.grain.compiler.types;

import com._7aske.grain.compiler.lexer.TokenType;

public enum AstRelationalOperator {
	GT,
	LT,
	GE,
	LE;

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
