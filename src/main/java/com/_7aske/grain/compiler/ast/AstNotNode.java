package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstNotNode extends AstUnaryNode {

	@Override
	public void run(Interpreter interpreter) {
		getNode().run(interpreter);
	}

	@Override
	public Object value() {
		return !(Boolean) getNode().value();
	}

	@Override
	public int getPrecedence() {
		return 1000;
	}
}
