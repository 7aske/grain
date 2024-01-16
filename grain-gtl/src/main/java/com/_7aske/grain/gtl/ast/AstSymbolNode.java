package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

public class AstSymbolNode extends AstNode {
	String symbolName;

	public AstSymbolNode() {
	}

	public AstSymbolNode(String name) {
		this.symbolName = name;
	}

	public String getName() {
		return symbolName;
	}

	public void setName(String name) {
		this.symbolName = name;
	}

	@Override
	public Object run(Interpreter interpreter) {
		return interpreter.getSymbolValue(symbolName);
	}
}
