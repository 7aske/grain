package com._7aske.grain.http.view.ast;

public abstract class AstNode {
	protected AstNode left;
	protected AstNode right;

	protected AstNode() {
	}

	public AstNode getLeft() {
		return left;
	}

	public void setLeft(AstNode left) {
		this.left = left;
	}

	public AstNode getRight() {
		return right;
	}

	public void setRight(AstNode right) {
		this.right = right;
	}
}
