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
		switch (operator) {
			case AND:
				// AND short-circuit
				if (!Boolean.parseBoolean(String.valueOf(leftValue)))
					return false;
				return Boolean.parseBoolean(String.valueOf(right.run(interpreter)));
			case OR:
				// OR short-circuit
				if (Boolean.parseBoolean(String.valueOf(leftValue)))
					return true;
				return Boolean.parseBoolean(String.valueOf(right.run(interpreter)));
		}
		throw new IllegalStateException("Unknown operator value " + operator);
	}
}
