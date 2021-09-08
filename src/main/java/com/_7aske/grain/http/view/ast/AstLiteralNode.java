package com._7aske.grain.http.view.ast;

import com._7aske.grain.http.view.ast.types.AstLiteralType;

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
	public String toString() {
		return "AstLiteralNode{" +
				"type=" + type +
				", value=" + value +
				'}';
	}
}
