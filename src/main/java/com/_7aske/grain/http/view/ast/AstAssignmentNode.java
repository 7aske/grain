package com._7aske.grain.http.view.ast;

public class AstAssignmentNode extends AstNode {

	public AstAssignmentNode() {
	}

	public AstAssignmentNode(AstSymbolNode symbol, AstNode value) {
		this.left = symbol;
		this.right = value;
	}

	public AstSymbolNode getSymbol() {
		return (AstSymbolNode) left;
	}

	public void setSymbol(AstSymbolNode symbol) {
		this.left = symbol;
	}

	public AstNode getValue() {
		return right;
	}

	public void setValue(AstNode value) {
		this.right = value;
	}

	@Override
	public String toString() {
		return "AstAssignmentNode{" +
				"left=" + left +
				", right=" + right +
				'}';
	}
}
