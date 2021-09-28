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
			switch (operator) {
				case ADD:
					return left.value().toString() + right.value().toString();
				case SUB:
				case DIV:
				case MUL:
					return left.value().toString().repeat(Integer.parseInt(right.value().toString()));
				case MOD:
				default:
					throw new RuntimeException("Unsupported operation for:\nleft:" + leftValue.getClass() + "\nright: " + rightValue.getClass());
			}
		} else {
			try {
				Float leftAsFloat = Float.parseFloat(String.valueOf(leftValue));
				Float rightAsFloat = Float.parseFloat(String.valueOf(rightValue));

				switch (operator) {
					case ADD:
						return leftAsFloat + rightAsFloat;
					case SUB:
						return leftAsFloat - rightAsFloat;
					case DIV:
						return leftAsFloat / rightAsFloat;
					case MUL:
						return leftAsFloat * rightAsFloat;
					case MOD:
						return leftAsFloat % rightAsFloat;
					default:
						throw new RuntimeException("Unsupported operation for:\nleft:" + leftValue.getClass() + "\nright: " + rightValue.getClass());
				}
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				throw ex;
			}
		}
	}
}
