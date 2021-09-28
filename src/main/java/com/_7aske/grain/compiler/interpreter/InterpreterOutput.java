package com._7aske.grain.compiler.interpreter;

public class InterpreterOutput {
	private final StringBuilder content;

	public InterpreterOutput() {
		this.content = new StringBuilder();
	}

	public void write(CharSequence text) {
		content.append(text);
	}

	public String getContent() {
		return content.toString();
	}
}
