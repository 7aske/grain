package com._7aske.grain.compiler.ast.basic;

import com._7aske.grain.compiler.interpreter.Interpreter;

public abstract class AstFlowControlNode extends AstNode {

	@Override
	public Object run(Interpreter interpreter) {
		return this;
	}
}
