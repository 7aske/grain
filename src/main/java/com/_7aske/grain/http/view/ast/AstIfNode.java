package com._7aske.grain.http.view.ast;

public class AstIfNode extends AstNode {
	private AstNode condition;

	public AstIfNode() {
	}

	public AstIfNode(AstNode condition, AstNode ifTrue, AstNode ifFalse) {
		this.condition = condition;
		this.left = ifTrue;
		this.right = ifFalse;
	}

	public AstNode getCondition() {
		return condition;
	}

	public void setCondition(AstNode condition) {
		this.condition = condition;
	}

	public AstNode getIfTrue() {
		return left;
	}

	public void setIfTrue(AstNode ifTrue) {
		this.left = ifTrue;
	}

	public AstNode getIfFalse() {
		return right;
	}

	public void setIfFalse(AstNode ifFalse) {
		this.right = ifFalse;
	}

	@Override
	public String toString() {
		return "AstIfNode{" +
				"condition=" + condition +
				// ", left=" + left +
				// ", right=" + right +
				'}';
	}
}
