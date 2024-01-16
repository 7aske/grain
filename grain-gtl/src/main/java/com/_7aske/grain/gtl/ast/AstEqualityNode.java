package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstBinaryNode;
import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;
import com._7aske.grain.gtl.ast.types.AstEqualityOperator;

import java.util.Objects;

public class AstEqualityNode extends AstBinaryNode {
	private AstEqualityOperator operator;

	public AstEqualityNode() {
	}

	public AstEqualityNode(AstEqualityOperator operator) {
		this.operator = operator;
	}

	public AstEqualityNode(AstEqualityOperator operator, AstNode left, AstNode right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public AstEqualityOperator getOperator() {
		return operator;
	}

	public void setOperator(AstEqualityOperator operator) {
		this.operator = operator;
	}

	@Override
	public int getPrecedence() {
		return operator.getPrecedance();
	}

	@Override
	public Object run(Interpreter interpreter) {
		Object leftValue = left.run(interpreter);
		Object rightValue = right.run(interpreter);

		switch (this.operator) {
			case EQ:
				return Objects.equals(leftValue, rightValue);
			case NE:
				return !Objects.equals(leftValue, rightValue);

		}
		throw new IllegalStateException("Unknown operator value " + operator);
	}
}
