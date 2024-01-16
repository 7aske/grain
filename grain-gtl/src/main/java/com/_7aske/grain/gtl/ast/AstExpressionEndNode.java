package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

public class AstExpressionEndNode extends AstNode {

	public AstExpressionEndNode() {
	}

	@Override
	public Object run(Interpreter interpreter) {
		return this;
	}
}
