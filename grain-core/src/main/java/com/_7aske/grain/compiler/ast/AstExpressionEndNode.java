package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstExpressionEndNode extends AstNode {

	public AstExpressionEndNode() {
	}

	@Override
	public Object run(Interpreter interpreter) {
		return this;
	}
}
