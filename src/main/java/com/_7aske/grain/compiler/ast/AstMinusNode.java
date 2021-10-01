package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstMinusNode extends AstUnaryNode {
	public static final int PRECEDENCE = 5000;

	@Override
	public Object run(Interpreter interpreter) {
		Object value = node.run(interpreter);
		if (value instanceof AstLiteralNode) {
			switch (((AstLiteralNode)value).getType()) {
				case FLOAT:
					return (-1) * (Float) ((AstLiteralNode) value).run(interpreter);
				case INTEGER:
					return (-1) * (Integer) ((AstLiteralNode) value).run(interpreter);
				default:
					throw new RuntimeException("Unsupported operation 'MINUS' for:\n" + value.getClass());
			}
		} else if (value instanceof Float) {
			return (-1) * (Float) value;
		} else if (value instanceof Integer) {
			return (-1) * (Integer) value;
		}
		throw new RuntimeException("Unsupported operation 'MINUS' for:\n" + value.getClass());
	}
}
