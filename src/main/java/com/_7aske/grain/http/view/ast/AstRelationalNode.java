package com._7aske.grain.http.view.ast;

import com._7aske.grain.http.view.ast.types.AstRelationalOperator;

public class AstRelationalNode extends AstNode {
	private AstRelationalOperator operator;

	public AstRelationalNode(AstRelationalOperator operator, AstNode left, AstNode right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public AstRelationalOperator getOperator() {
		return operator;
	}

	public void setOperator(AstRelationalOperator operator) {
		this.operator = operator;
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

	@Override
	public String toString() {
		return "AstRelationalNode{" +
				"left=" + left +
				", right=" + right +
				", operator=" + operator +
				'}';
	}
}
