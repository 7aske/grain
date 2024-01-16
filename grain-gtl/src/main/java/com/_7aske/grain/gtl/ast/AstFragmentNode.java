package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

import java.util.List;

public class AstFragmentNode extends AstNode {
	private String content;
	private AstSymbolNode symbol;
	private List<AstNode> arguments;

	public AstFragmentNode(String content) {
		this.content = content;
	}

	public AstSymbolNode getSymbol() {
		return symbol;
	}

	public void setSymbol(AstSymbolNode symbol) {
		this.symbol = symbol;
	}

	public List<AstNode> getArguments() {
		return arguments;
	}

	public void setArguments(List<AstNode> arguments) {
		this.arguments = arguments;
	}

	@Override
	public Object run(Interpreter interpreter) {
		// @Incomplete we need to parse arguments
		return content;
	}
}
