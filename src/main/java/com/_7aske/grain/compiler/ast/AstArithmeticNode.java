package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.types.AstArithmeticOperator;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstArithmeticNode extends AstBinaryNode {
	private AstArithmeticOperator operator;

	public AstArithmeticNode(AstArithmeticOperator operator) {
		this.operator = operator;
	}

	public AstArithmeticOperator getOperator() {
		return operator;
	}

	public void setOperator(AstArithmeticOperator operator) {
		this.operator = operator;
	}

	@Override
	public int getPrecedence() {
		return operator.getPrecedance();
	}

	@Override
	public void run(Interpreter interpreter) {
		this.left.run(interpreter);
		this.right.run(interpreter);
	}

	@Override
	public Object value() {
		Object leftValue = left.value();
		Object rightValue = right.value();
		boolean stringOperation = leftValue instanceof String || rightValue instanceof String;

		if (stringOperation) {
			return getStringOperation(leftValue, rightValue);
		} else {
			boolean isFloatOperation = leftValue instanceof Float || rightValue instanceof Float;
			if (isFloatOperation) {
				Float leftAsNumber = Float.parseFloat(String.valueOf(leftValue));
				Float rightAsNumber = Float.parseFloat(String.valueOf(rightValue));
				return getNumberOperation(leftAsNumber, rightAsNumber);
			} else {
				Integer leftAsNumber = Integer.parseInt(String.valueOf(leftValue));
				Integer rightAsNumber = Integer.parseInt(String.valueOf(rightValue));
				return getNumberOperation(leftAsNumber, rightAsNumber);
			}
		}
	}

	private String getStringOperation(Object left, Object right) {
		switch (operator) {
			case ADD:
				return left.toString() + right.toString();
			case MUL:
				if (left instanceof String) {
					return left.toString().repeat(Integer.parseInt(right.toString()));
				} else if (right instanceof String) {
					return right.toString().repeat(Integer.parseInt(left.toString()));
				} else {
					throw new RuntimeException("Unsupported operation for:\nleft:" + left.getClass() + "\nright: " + right.getClass());
				}
			default:
				throw new RuntimeException("Unsupported operation for:\nleft:" + left.getClass() + "\nright: " + right.getClass());
		}
	}

	private Float getNumberOperation(Float left, Float right) {
		switch (operator) {
			case ADD:
				return left + right;
			case SUB:
				return left - right;
			case DIV:
				return left / right;
			case MUL:
				return left * right;
			case MOD:
				return left % right;
			default:
				throw new RuntimeException("Unsupported operation for:\nleft:" + left.getClass() + "\nright: " + right.getClass());
		}
	}

	private Integer getNumberOperation(Integer left, Integer right) {
		switch (operator) {
			case ADD:
				return left + right;
			case SUB:
				return left - right;
			case DIV:
				return left / right;
			case MUL:
				return left * right;
			case MOD:
				return left % right;
			default:
				throw new RuntimeException("Unsupported operation for:\nleft:" + left.getClass() + "\nright: " + right.getClass());
		}
	}
}
