package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

public class AstForNode extends AstNode {
	private AstNode condition;
	private AstNode initialization;
	private AstNode increment;
	private AstNode body;

	public AstForNode() {
	}

	public AstForNode(AstNode initialization, AstNode condition, AstNode increment, AstNode body) {
		this.condition = condition;
		this.initialization = initialization;
		this.increment = increment;
		this.body = body;
	}

	public AstNode getCondition() {
		return condition;
	}

	public void setCondition(AstNode condition) {
		this.condition = condition;
	}

	public AstNode getInitialization() {
		return initialization;
	}

	public void setInitialization(AstNode initialization) {
		this.initialization = initialization;
	}

	public AstNode getIncrement() {
		return increment;
	}

	public void setIncrement(AstNode ifFalse) {
		this.increment = ifFalse;
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
		interpreter.pushScope();
		if (this.getInitialization() != null)
			this.getInitialization().run(interpreter);
		boolean shouldSkip = false;
		while (evaluateCondition(interpreter)) {
			if (shouldSkip) {
				shouldSkip = false;
				continue;
			}
			value = this.getBody().run(interpreter);
			if (value instanceof AstContinueNode)
				shouldSkip = true;
			if (value instanceof AstBreakNode)
				break;
			if (this.getIncrement() != null)
				this.getIncrement().run(interpreter);
		}
		interpreter.popScope();
		return value;
	}

	private Boolean evaluateCondition(Interpreter interpreter) {
		if (this.getCondition() == null) return true;
		Object value = this.getCondition().run(interpreter);
		return (Boolean) value;
	}
}
