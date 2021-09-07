package com._7aske.grain.http.view.ast;

import com._7aske.grain.http.view.ast.types.AstEqualityOperator;

public class AstEqualityNode extends AstNode {
	private AstEqualityOperator operator;

	public AstEqualityNode() {
	}

	public AstEqualityNode(AstEqualityOperator operator) {
		this.operator = operator;
	}

	public AstEqualityNode(AstEqualityOperator operator, AstNode left, AstNode right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
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

	public AstEqualityOperator getOperator() {
		return operator;
	}

	public void setOperator(AstEqualityOperator operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return "AstEqualityNode{" +
				"operator=" + operator +
				", left=" + left +
				", right=" + right +
				'}';
	}
}
