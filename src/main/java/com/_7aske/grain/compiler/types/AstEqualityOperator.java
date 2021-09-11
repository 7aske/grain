package com._7aske.grain.compiler.types;

import com._7aske.grain.compiler.lexer.TokenType;

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
