package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstExpressionEndNode extends AstNode {

	public AstExpressionEndNode() {
	}

	@Override
	public void run(Interpreter interpreter) {

	}

	@Override
	public Object value() {
		return null;
	}
}
