package com._7aske.grain.compiler.ast;

public class AstUnaryNode extends AstNode {
	protected AstNode node;

	protected AstUnaryNode() {
	}

	public AstNode getNode() {
		return node;
	}

	public void setNode(AstNode node) {
		this.node = node;
	}
}
