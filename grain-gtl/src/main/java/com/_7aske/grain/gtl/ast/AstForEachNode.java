package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

public class AstForEachNode extends AstNode {
	private AstForEachIteratorNode iterator;
	private AstNode body;

	public AstForEachNode() {
	}

	public AstForEachNode(AstForEachIteratorNode iterator, AstNode body) {
		this.iterator = iterator;
		this.body = body;
	}

	public AstForEachIteratorNode getIterator() {
		return iterator;
	}

	public void setIterator(AstForEachIteratorNode iterator) {
		this.iterator = iterator;
	}

	public AstNode getBody() {
		return body;
	}

	public void setBody(AstNode body) {
		this.body = body;
	}

	@Override
	public Object run(Interpreter interpreter) {
		Object value = null;
		Object result;
		interpreter.pushScope();
		boolean shouldSkip = false;
		while (true) {
			if (shouldSkip) {
				shouldSkip = false;
				continue;
			}
			result = iterator.run(interpreter);
			if (result instanceof AstContinueNode)
				shouldSkip = true;
			if (result instanceof AstBreakNode)
				break;
			interpreter.putCurrentScopeSymbol(iterator.getSymbol().getName(), result);
			value = this.getBody().run(interpreter);
			if (value instanceof AstContinueNode)
				shouldSkip = true;
			if (value instanceof AstBreakNode)
				break;
		}
		interpreter.popScope();
		return value;
	}
}
