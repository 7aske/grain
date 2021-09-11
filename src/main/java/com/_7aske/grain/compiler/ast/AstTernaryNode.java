package com._7aske.grain.compiler.ast;

public class AstTernaryNode extends AstNode {
	protected AstNode condition;
	protected AstNode left;
	protected AstNode right;

	protected AstTernaryNode() {
	}

	public AstNode getCondition() {
		return condition;
	}

	public void setCondition(AstNode condition) {
		this.condition = condition;
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
