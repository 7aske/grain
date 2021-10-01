package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.ast.types.AstBooleanOperator;

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
	public AstBooleanOperator getOperator() {
		return operator;
	}

	public void setOperator(AstBooleanOperator operator) {
		this.operator = operator;
	}

	@Override
	public int getPrecedence() {
		return operator.getPrecedence();
	}

	@Override
	public Object run(Interpreter interpreter) {
		Object leftValue = left.run(interpreter);
		Object rightValue = right.run(interpreter);
		switch (operator) {
			case AND:
				return Boolean.parseBoolean(String.valueOf(leftValue)) && Boolean.parseBoolean(String.valueOf(rightValue));
			case OR:
				return Boolean.parseBoolean(String.valueOf(leftValue)) || Boolean.parseBoolean(String.valueOf(rightValue));
		}
		throw new IllegalStateException("Unknown operator value " + operator);
	}
}
