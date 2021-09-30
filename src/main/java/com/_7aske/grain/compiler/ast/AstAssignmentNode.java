package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

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
	public void run(Interpreter interpreter) {
		left.run(interpreter);
		right.run(interpreter);
		if (this.left instanceof AstArrayIndexNode) {
			((AstArrayIndexNode) this.left).setValue(this.right);
		} else {
			interpreter.putScopedSymbol(((AstSymbolNode) left).getName(), right.value());
		}
	}

	@Override
	public Object value() {
		return this.right.value();
	}
}
