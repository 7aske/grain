package com._7aske.grain.http.view.ast;

public abstract class AstNode {
	private AstNode parent;

	public AstNode getParent() {
		return parent;
	}

	public void setParent(AstNode parent) {
		this.parent = parent;
	}
}
