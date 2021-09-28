package com._7aske.grain.compiler.ast.types;

import com._7aske.grain.compiler.lexer.TokenType;

public enum AstBooleanOperator {
	AND(500),
	OR(500);

	private final int precedence;

	AstBooleanOperator(int precedence) {
		this.precedence = precedence;
	}

	public int getPrecedence() {
		return precedence;
	}

	public static AstBooleanOperator from(TokenType typ) {
		switch (typ) {
			case AND:
				return AND;
			case OR:
				return OR;
		}
		return null;
	}
}
