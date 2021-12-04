package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.List;

public class AstForEachIteratorNode extends AstNode {
	private AstSymbolNode symbol;
	private AstSymbolNode iterator;
	private List<?> value = null;
	private int current = 0;

	public AstForEachIteratorNode() {
	}

	public AstForEachIteratorNode(AstSymbolNode item, AstSymbolNode iterator) {
		this.iterator = iterator;
		this.symbol = item;
	}

	public AstSymbolNode getSymbol() {
		return symbol;
	}

	public void setSymbol(AstSymbolNode symbol) {
		this.symbol = symbol;
	}

	public AstSymbolNode getIterator() {
		return iterator;
	}

	public void setIterator(AstSymbolNode iterator) {
		this.iterator = iterator;
	}

	@Override
	public Object run(Interpreter interpreter) {
		if (value == null) {
			// @Refactor this can be an actual iterator
			value = (List<?>) this.iterator.run(interpreter);
		}

		if (current == value.size()) {
			return new AstBreakNode();
		}

		return value.get(current++);
	}
}
