package com._7aske.grain.http.view.ast.parser.exception;

public class ParserSyntaxErrorException extends RuntimeException {
	public ParserSyntaxErrorException(String message, Object... args) {
		super(String.format(message, args));
	}
}
