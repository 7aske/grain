package com._7aske.grain.compiler.ast.basic;

import com._7aske.grain.compiler.interpreter.Interpreter;

public abstract class AstNode {
	private AstNode parent;
	private int precedence = 0;

	public AstNode getParent() {
		return parent;
	}

	public void setParent(AstNode parent) {
		this.parent = parent;
	}

	public abstract void run(Interpreter interpreter);

	public abstract Object value();

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	public int getPrecedence() {
		return precedence;
	}
}
