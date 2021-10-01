package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstObjectReferenceNode extends AstSymbolNode {
	private AstNode reference;
	private Object backReference;
	private String name;

	public AstObjectReferenceNode() {
	}

	public AstObjectReferenceNode(String name) {
		this.name = name;
	}

	public Object getBackReference() {
		return backReference;
	}

	public void setBackReference(Object backReference) {
		this.backReference = backReference;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		if (this.reference instanceof AstObjectReferenceNode) {
			return this.name + "." + ((AstObjectReferenceNode) this.reference).getFullName();
		} else {
			return this.name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReference(AstNode value) {
		this.reference = value;
	}

	public AstNode getReference() {
		return this.reference;
	}

	// TODO: refactor
	@Override
	public Object run(Interpreter interpreter) {
		Object value = null;
		if (this.backReference == null) {
			value = interpreter.getSymbolValue(this.name);
		} else {
			if (this.backReference instanceof AstObjectReferenceNode) {
				Object backReferenceValue = ((AstObjectReferenceNode) this.backReference).run(interpreter);
				try {
					value = backReferenceValue.getClass().getField(this.name).get(backReferenceValue);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (this.reference instanceof AstObjectReferenceNode) {
			((AstObjectReferenceNode) this.reference).setBackReference(value);
			value = this.reference.run(interpreter);
		} else if (this.reference instanceof AstFunctionCallNode) {
			((AstFunctionCallNode) this.reference).setBackReference(value);
			value = this.reference.run(interpreter);
		}

		return value;
	}
}
