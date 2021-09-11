package com._7aske.grain.compiler.ast;

public abstract class AstNode {
	private AstNode parent;

	public AstNode getParent() {
		return parent;
	}

	public void setParent(AstNode parent) {
		this.parent = parent;
	}
}
