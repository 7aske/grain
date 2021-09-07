package com._7aske.grain.http.view.ast.types;

import com._7aske.grain.http.view.ast.lexer.TokenType;

public enum AstLiteralType {
	STRING,
	FLOAT,
	INTEGER,
	BOOLEAN,
	NULL;

	public static AstLiteralType from(TokenType typ) {
		switch (typ) {
			case LIT_FLT:
				return FLOAT;
			case LIT_STR:
				return STRING;
			case LIT_INT:
				return INTEGER;
		}
		return null;
	}
}
