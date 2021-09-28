package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.ast.types.AstRelationalOperator;

public class AstRelationalNode extends AstBinaryNode {
	private AstRelationalOperator operator;

	public AstRelationalNode(AstRelationalOperator operator) {
		this.operator = operator;
	}

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
	public void run(Interpreter interpreter) {
		left.run(interpreter);
		right.run(interpreter);
	}

	@Override
	public Object value() {
		Double leftValue = Double.parseDouble(String.valueOf(this.left.value()));
		Double rightValue = Double.parseDouble(String.valueOf(this.right.value()));
		switch (this.operator) {
			case GT:
				return leftValue > rightValue;
			case LT:
				return leftValue < rightValue;
			case GE:
				return leftValue >= rightValue;
			case LE:
				return leftValue <= rightValue;
		}
		throw new IllegalStateException("Unknown operator value " + operator);
	}
}
