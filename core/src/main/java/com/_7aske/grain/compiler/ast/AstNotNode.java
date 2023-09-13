package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstNotNode extends AstUnaryNode {
	public static final int PRECEDENCE = 5000;

	@Override
	public int getPrecedence() {
		return PRECEDENCE;
	}

	@Override
	public Object run(Interpreter interpreter) {
		return !(Boolean) getNode().run(interpreter);
	}
}