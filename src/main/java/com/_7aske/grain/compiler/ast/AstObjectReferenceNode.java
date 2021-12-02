package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.lang.reflect.Field;

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

	// @Refactor can keep a state information about whether the node has been
	// evaluated or not so that the node that is referencing doesn't need to
	// call .run() method. Also since the parser is now properly setting back
	// reference attrs we shouldn't ever forward current node value to its
	// reference by setting the back reference field ourselves.
	@Override
	public Object run(Interpreter interpreter) {
		Object value = null;
		if (this.backReference == null) {
			value = interpreter.getSymbolValue(this.name);
		} else {
			if (!(this.backReference instanceof AstNode)) {
				try {
					Field field = this.backReference.getClass().getDeclaredField(this.name);
					field.setAccessible(true);
					value = field.get(this.backReference);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					e.printStackTrace();
				}
			} else {
				// Wierd
				throw new RuntimeException(String.format("Unable to parse reference %s", this.backReference.getClass()));
			}
		}

		// After parsing our value we run the reference
		if (value == null && this.reference != null) {
			throw new NullPointerException(String.format("Cannot read attribute '%s' of null", this.name));
		} else if (value == null) {
			return null;
		} else if (this.reference instanceof AstObjectReferenceNode) {
			// @Refactor avoid forwarding the value to the referencing node
			((AstObjectReferenceNode) this.reference).setBackReference(value);
			value = this.reference.run(interpreter);
		} else if (this.reference instanceof AstFunctionCallNode) {
			// @Refactor avoid forwarding the value to the referencing node
			((AstFunctionCallNode) this.reference).setBackReference(value);
			value = this.reference.run(interpreter);
		} else if (this.reference instanceof AstSymbolNode) {
			try {
				Field field = value.getClass().getDeclaredField(((AstSymbolNode) this.reference).symbolName);
				field.setAccessible(true);
				value = field.get(value);
			} catch (IllegalAccessException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}

		return value;
	}
}
