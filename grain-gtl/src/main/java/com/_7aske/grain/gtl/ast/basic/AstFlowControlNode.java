package com._7aske.grain.gtl.ast.basic;

import com._7aske.grain.gtl.interpreter.Interpreter;

public abstract class AstFlowControlNode extends AstNode {

	@Override
	public Object run(Interpreter interpreter) {
		return this;
	}
}
