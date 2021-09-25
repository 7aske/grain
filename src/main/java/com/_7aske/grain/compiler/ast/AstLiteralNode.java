package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.types.AstLiteralType;

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

	@Override
	public void run(Interpreter interpreter) {

	}

	@Override
	public Object value() {
		if (type == AstLiteralType.BOOLEAN) {
			return Boolean.parseBoolean((String) value);
		} else if (type == AstLiteralType.FLOAT) {
			return Float.parseFloat((String) value);
		} else if (type == AstLiteralType.INTEGER) {
			return Integer.parseInt((String) value);
		} else if (type == AstLiteralType.NULL) {
			return null;
		}
		return value;
	}

}
