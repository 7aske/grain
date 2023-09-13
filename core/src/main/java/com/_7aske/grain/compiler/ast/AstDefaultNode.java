package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstDefaultNode extends AstBinaryNode {

	public AstDefaultNode() {
	}

	public AstNode getValue() {
		return left;
	}

	public void setValue(AstNode left) {
		this.left = left;
	}

	public AstNode getDefault() {
		return right;
	}

	public void setDefault(AstNode right) {
		this.right = right;
	}
	@Override
	public Object run(Interpreter interpreter) {
		Object value = getValue().run(interpreter);
		if (value == null)
			value = getDefault().run(interpreter);
		return value;
	}
}
