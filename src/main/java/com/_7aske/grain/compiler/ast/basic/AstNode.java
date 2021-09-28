package com._7aske.grain.compiler.ast.basic;

import com._7aske.grain.compiler.interpreter.Interpreter;

public abstract class AstNode {
	private AstNode parent;

	public AstNode getParent() {
		return parent;
	}

	public void setParent(AstNode parent) {
		this.parent = parent;
	}

	public abstract void run(Interpreter interpreter);

	public int getPrecedence() {
		return Integer.MIN_VALUE;
	}

	public abstract Object value();
}
