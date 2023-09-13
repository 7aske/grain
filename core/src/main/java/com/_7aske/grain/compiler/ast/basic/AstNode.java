package com._7aske.grain.compiler.ast.basic;

import com._7aske.grain.compiler.interpreter.Interpreter;

public abstract class AstNode {
	// @Todo make run throw generic InterpreterRuntimeException
	public abstract Object run(Interpreter interpreter);

	public int getPrecedence() {
		return Integer.MIN_VALUE;
	}
}
