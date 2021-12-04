package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstKeywordArgumentNode extends AstBinaryNode {

	public AstKeywordArgumentNode() {
	}

	public AstKeywordArgumentNode(AstSymbolNode symbolNode, AstNode value) {
		this.left = symbolNode;
		this.right = value;
	}

	public AstSymbolNode getSymbol() {
		return (AstSymbolNode) this.getLeft();
	}

	public void setSymbol(AstSymbolNode astSymbolNode) {
		this.setLeft(astSymbolNode);
	}


	public void setValue(AstNode value) {
		this.setRight(value);
	}

	public AstNode getValue() {
		return this.getRight();
	}

	@Override
	public Object run(Interpreter interpreter) {
		return null;
	}
}
