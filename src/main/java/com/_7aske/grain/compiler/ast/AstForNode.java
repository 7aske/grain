package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstTernaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

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
	public void run(Interpreter interpreter) {
		if (this.getInitialization() != null)
			this.getInitialization().run(interpreter);
		while (evaluateCondition(interpreter)) {
			this.getBody().run(interpreter);
			if (this.getIncrement() != null)
				this.getIncrement().run(interpreter);
		}
	}

	private Boolean evaluateCondition(Interpreter interpreter) {
		if (this.getCondition() == null) return true;
		this.getCondition().run(interpreter);
		return (Boolean) this.getCondition().value();
	}

	@Override
	public Object value() {
		return this.condition.value();
	}

}
