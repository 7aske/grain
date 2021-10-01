package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.ast.types.AstLiteralType;

public class AstLiteralNode extends AstUnaryNode {
	private AstLiteralType type;
	private Object value;

	public AstLiteralNode() {
	}

	public AstLiteralNode(AstLiteralType type, Object value) {
		this.type = type;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public AstLiteralType getType() {
		return type;
	}

	public void setType(AstLiteralType type) {
		this.type = type;
	}

	@Override
	public Object run(Interpreter _ignored) {
		if (type == AstLiteralType.BOOLEAN) {
			return Boolean.parseBoolean((String) value);
		} else if (type == AstLiteralType.FLOAT) {
			return Float.parseFloat((String) value);
		} else if (type == AstLiteralType.INTEGER) {
			return Integer.parseInt((String) value);
		} else if (type == AstLiteralType.STRING) {
			return value;
		} else if (type == AstLiteralType.NULL) {
			return null;
		}
		throw new IllegalStateException("Unknown literal type " +  type);
	}
}
