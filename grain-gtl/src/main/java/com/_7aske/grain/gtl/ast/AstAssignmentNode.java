package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstBinaryNode;
import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

public class AstAssignmentNode extends AstBinaryNode {
	public static final int PRECEDENCE = 100;

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

	public void setSymbol(AstNode symbol) {
		this.left = symbol;
	}

	public AstNode getValue() {
		return right;
	}

	public void setValue(AstNode value) {
		this.right = value;
	}

	@Override
	public int getPrecedence() {
		return PRECEDENCE;
	}

	@Override
	public Object run(Interpreter interpreter) {
		left.run(interpreter);
		Object rightValue = right.run(interpreter);

		if (this.left instanceof AstArrayIndexNode) {
			((AstArrayIndexNode) left).setValue(rightValue);
		} else {
			interpreter.putScopedSymbol(((AstSymbolNode) left).getName(), rightValue);
		}

		return rightValue;
	}
}
