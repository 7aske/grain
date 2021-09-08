package com._7aske.grain.http.view.ast;

import com._7aske.grain.http.view.ast.types.AstBooleanOperator;

public class AstBooleanNode extends AstBinaryNode {
	private AstBooleanOperator operator;

	public AstBooleanNode() {
	}

	public AstBooleanNode(AstBooleanOperator operator, AstNode left, AstNode right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public AstBooleanNode(AstBooleanOperator operator) {
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

	public AstBooleanOperator getOperator() {
		return operator;
	}

	public void setOperator(AstBooleanOperator operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return "AstBooleanNode{" +
				"operator=" + operator +
				'}';
	}
}
