package com._7aske.grain.compiler.ast;

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
		return value;
	}

	@Override
	public String toString() {
		return "AstLiteralNode{" +
				"type=" + type +
				", value=" + value +
				'}';
	}
}
