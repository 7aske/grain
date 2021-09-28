package com._7aske.grain.compiler.ast.types;

import com._7aske.grain.compiler.lexer.TokenType;

public enum AstArithmeticOperator {
	ADD(1000),
	SUB(1000),
	DIV(2000),
	MUL(2000),
	MOD(2000);

	private final int precedance;

	AstArithmeticOperator(int precedence) {
		this.precedance = precedence;
	}

	public int getPrecedance() {
		return precedance;
	}

	public static AstArithmeticOperator from(TokenType type) {
		switch (type) {
			case ADD:
				return ADD;
			case SUB:
				return SUB;
			case DIV:
				return DIV;
			case MUL:
				return MUL;
			case MOD:
				return MOD;
			default:
				throw new RuntimeException("Invalid type " + type);
		}
	}
}
