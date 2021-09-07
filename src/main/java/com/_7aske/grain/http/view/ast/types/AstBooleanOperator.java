package com._7aske.grain.http.view.ast.types;

import com._7aske.grain.http.view.ast.lexer.TokenType;

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
