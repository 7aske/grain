package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstObjectReferenceNode extends AstSymbolNode {
	Object value;
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

	@Override
	public void run(Interpreter interpreter) {
		if (this.backReference == null) {
			this.value = interpreter.getSymbolValue(this.name);
		} else {
			if (this.backReference instanceof AstObjectReferenceNode && ((AstObjectReferenceNode) this.backReference).value != null) {
				try {
					this.value = ((AstObjectReferenceNode) this.backReference).value.getClass().getField(this.name).get(((AstObjectReferenceNode) this.backReference).value);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
		}

		if (this.reference instanceof AstObjectReferenceNode) {
			((AstObjectReferenceNode) this.reference).setBackReference(this);
			this.reference.run(interpreter);
		} else if (this.reference instanceof AstFunctionCallNode) {
			((AstFunctionCallNode) this.reference).setBackReference(this.value);
			this.reference.run(interpreter);
		}
	}

	@Override
	public Object value() {
		return this.reference.value();
	}
}
