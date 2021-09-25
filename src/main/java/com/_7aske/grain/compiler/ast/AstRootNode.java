package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstRootNode extends AstUnaryNode {

	@Override
	public void run(Interpreter interpreter) {
		getNode().run(interpreter);
	}

	@Override
	public Object value() {
		return getNode().value();
	}
}
