package com._7aske.grain.compiler.types;

import com._7aske.grain.compiler.lexer.TokenType;

public enum AstBooleanOperator {
	AND,
	OR;

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
