package com._7aske.grain.compiler.lexer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TokenType {
	IDEN(null),
	LIT_STR(null),
	LIT_INT(null),
	LIT_FLT(null),

	// keywords
	IF("if"),
	THEN("then"),
	ELSE("else"),
	FOR("for"),
	FOREACH("foreach"),
	IN("in"),
	CONTINUE("continue"),
	BREAK("break"),
	TRUE("true"),
	FALSE("false"),
	NULL("null"),
	IMPORT("import"),

	// operators
	ASSN("="),
	DFLT("??"),

	// arithmetic
	ADD("+"),
	SUB("-"),
	DIV("/"),
	MUL("*"),
	MOD("%"),

	// logic
	AND("&&"),
	OR("||"),
	NOT("!"),
	EQ("=="),
	NE("!="),
	GT(">"),
	LT("<"),
	GE(">="),
	LE("<="),

	// unary
	INC("++"),
	DEC("--"),

	// separators
	DOT("."),
	COMMA(","),
	SPACE(" "),
	TAB("\t"),
	LF("\n"),
	SCOL(";"),

	// parenthesis
	LPAREN("("),
	RPAREN(")"),
	LBRACE("{"),
	RBRACE("}"),
	LBRACK("["),
	RBRACK("]"),

	_START(null),
	_END(null),
	INVALID(null);

	public static final Map<String, TokenType> values = Arrays.stream(values())
			.filter(type -> type.value != null)
			.collect(Collectors.toMap(TokenType::getValue, Function.identity()));

	private final String value;

	TokenType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
