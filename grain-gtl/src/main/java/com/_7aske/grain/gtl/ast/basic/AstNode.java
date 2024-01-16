package com._7aske.grain.gtl.ast.basic;

import com._7aske.grain.gtl.interpreter.Interpreter;

public abstract class AstNode {
	// @Todo make run throw generic InterpreterRuntimeException
	public abstract Object run(Interpreter interpreter);

	public int getPrecedence() {
		return Integer.MIN_VALUE;
	}
}
