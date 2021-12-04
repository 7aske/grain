package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.interpreter.exception.InterpreterInvalidIteratorException;
import com._7aske.grain.compiler.interpreter.exception.InterpreterNullPointerException;
import com._7aske.grain.util.formatter.StringFormat;
import com._7aske.grain.util.iterator.StringIterator;

import java.util.Iterator;

public class AstForEachIteratorNode extends AstNode {
	private AstSymbolNode symbol;
	private AstNode iterator;
	private Object value = null;
	private Iterator<?> valueIterator = null;

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

	public AstNode getIterator() {
		return iterator;
	}

	public void setIterator(AstNode iterator) {
		this.iterator = iterator;
	}

	@Override
	public Object run(Interpreter interpreter) {
		if (value == null) {
			value = this.iterator.run(interpreter);
			if (value == null){
				throw new InterpreterNullPointerException("Cannot iterate over null value");
			}
			// We allow iteration over strings
			if (value instanceof String) {
				// Gotta love building own tools
				valueIterator = new StringIterator((String) value);
			} else if (!Iterable.class.isAssignableFrom(value.getClass())) {
				throw new InterpreterInvalidIteratorException(StringFormat.format("Type {} is not a valid Iterable<?> type", value.getClass()));
			} else {
				valueIterator = ((Iterable<?>) value).iterator();
			}

		}

		if (!valueIterator.hasNext()) {
			return new AstBreakNode();
		}

		return valueIterator.next();
	}
}
