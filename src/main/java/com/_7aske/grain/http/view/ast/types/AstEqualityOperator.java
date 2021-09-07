package com._7aske.grain.http.view.ast.types;

import com._7aske.grain.http.view.ast.lexer.TokenType;

public enum AstEqualityOperator {
	EQ,
	NE;

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
