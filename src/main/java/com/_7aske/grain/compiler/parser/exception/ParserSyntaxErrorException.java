package com._7aske.grain.compiler.parser.exception;

public class ParserSyntaxErrorException extends RuntimeException {
	private String positionalMessage;
	public ParserSyntaxErrorException(String message, Object... args) {
		super(String.format(message, args));
	}

	public ParserSyntaxErrorException(String positionalMessage, String message, Object... args) {
		super(String.format(message, args));
		this.positionalMessage = positionalMessage;
	}

	public String getPositionalMessage() {
		return positionalMessage;
	}
}
