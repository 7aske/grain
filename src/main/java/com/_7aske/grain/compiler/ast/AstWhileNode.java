package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstWhileNode extends AstBinaryNode {

	public AstWhileNode(AstNode condition, AstNode body) {
		this.left = condition;
		this.right = body;
	}

	public AstNode getCondition() {
		return left;
	}

	public void setCondition(AstNode condition) {
		this.right = condition;
	}

	public AstNode getBody() {
		return left;
	}

	public void setBody(AstNode body) {
		this.right = body;
	}

	@Override
	public void run(Interpreter interpreter) {

	}

	@Override
	public Object value() {
		return null;
	}
}
