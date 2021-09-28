package com._7aske.grain.compiler.ast.types;

import com._7aske.grain.compiler.lexer.TokenType;

public enum AstEqualityOperator {
	EQ(600),
	NE(600);

	private final int precedance;

	AstEqualityOperator(int precedance) {
		this.precedance = precedance;
	}

	public int getPrecedance() {
		return precedance;
	}

	public static AstEqualityOperator from(TokenType typ) {
		switch (typ) {
			case EQ:
				return EQ;
			case NE:
				return NE;
		}
		return null;
	}
}
