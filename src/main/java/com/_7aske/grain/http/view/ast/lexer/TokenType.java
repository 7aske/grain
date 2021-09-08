package com._7aske.grain.http.view.ast.lexer;

public enum TokenType {
	IDEN,
	LIT_STR,
	LIT_INT,
	LIT_FLT,

	// keywords
	IF,
	ELIF,
	ELSE,
	ENDIF,
	FOR,
	IN,
	CONTINUE,
	BREAK,
	TRUE,
	FALSE,
	NULL,

	// operators
	ASSN,

	// arithmetic
	ADD,
	SUB,
	DIV,
	MUL,
	MOD,

	// logic
	AND,
	OR,
	NOT,
	EQ,
	NE,
	GT,
	LT,
	GE,
	LE,

	// unary
	INC,
	DEC,

	// separators
	DOT,
	COMMA,
	SPACE,
	TAB,
	LF,
	SCOL,

	// parenthesis
	LPAREN,
	RPAREN,
	LBRACE,
	RBRACE,
	LBRACK,
	RBRACK,

	INVALID,
	_TOKEN_SIZE;

	public static String[] values = new String[] {
			null,
			null,
			null,
			null,
			"if",
			"elif",
			"else",
			"endif",
			"for",
			"in",
			"continue",
			"break",
			"true",
			"false",
			"null",
			"=",
			"+",
			"-",
			"/",
			"*",
			"%",
			"&&",
			"||",
			"!",
			"==",
			"!=",
			">",
			"<",
			">=",
			"<=",
			"++",
			"--",
			".",
			",",
			" ",
			"\t",
			"\n",
			";",
			"(",
			")",
			"{",
			"}",
			"[",
			"]",
			null,
	};
}
