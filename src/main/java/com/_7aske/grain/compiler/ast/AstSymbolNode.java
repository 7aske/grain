package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstSymbolNode extends AstNode {
	String name;
	Object value;

	public AstSymbolNode() {
	}

	public AstSymbolNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void run(Interpreter interpreter) {
		value = interpreter.getSymbolValue(name);
	}

	@Override
	public Object value() {
		return value;
	}
}
