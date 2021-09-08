package com._7aske.grain.http.view.ast;

public class AstBinaryNode extends AstNode {
	protected AstNode left;
	protected AstNode right;

	protected AstBinaryNode() {
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
