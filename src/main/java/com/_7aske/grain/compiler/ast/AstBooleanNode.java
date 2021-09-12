package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.types.AstBooleanOperator;

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
	public void run(Interpreter interpreter) {
		left.run(interpreter);
		right.run(interpreter);
	}

	@Override
	public Object value() {
		switch (operator) {
			case AND:
				return Boolean.parseBoolean(String.valueOf(left.value())) && Boolean.parseBoolean(String.valueOf(right.value()));
			case OR:
				return Boolean.parseBoolean(String.valueOf(left.value())) || Boolean.parseBoolean(String.valueOf(right.value()));
		}
		throw new IllegalStateException("Unknown operator value " + operator);
	}

	@Override
	public String toString() {
		return "AstBooleanNode{" +
				"operator=" + operator +
				'}';
	}
}