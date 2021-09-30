package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.List;

public class AstArrayIndexNode extends AstNode {
	private AstNode symbol;
	private AstNode index;

	public AstArrayIndexNode(AstNode symbol) {
		this.symbol = symbol;
	}

	public AstNode getSymbol() {
		return symbol;
	}

	public void setSymbol(AstNode symbol) {
		this.symbol = symbol;
	}

	public AstNode getIndex() {
		return index;
	}

	public void setIndex(AstNode index) {
		this.index = index;
	}

	@Override
	public void run(Interpreter interpreter) {
		this.symbol.run(interpreter);
		this.index.run(interpreter);
	}

	@Override
	public Object value() {
		return ((List<Object>)this.symbol.value()).get(Integer.parseInt(String.valueOf(this.index.value())));
	}

	public void setValue(Object value) {
		((List<Object>)this.symbol.value()).set(Integer.parseInt(String.valueOf(this.index.value())), value);
	}
}
