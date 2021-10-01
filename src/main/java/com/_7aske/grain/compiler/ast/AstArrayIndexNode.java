package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.List;

public class AstArrayIndexNode extends AstNode {
	private AstNode symbol;
	private Object symbolValue;
	private AstNode index;
	private Object indexValue;

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
	public Object run(Interpreter interpreter) {
		symbolValue = this.symbol.run(interpreter);
		indexValue = this.index.run(interpreter);
		return ((List<Object>) symbolValue).get(Integer.parseInt(String.valueOf(indexValue)));
	}

	public void setValue(Object value) {
		((List<Object>) symbolValue).set(Integer.parseInt(String.valueOf(indexValue)), value);
	}
}
