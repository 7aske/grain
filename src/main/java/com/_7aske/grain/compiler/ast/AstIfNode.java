package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstIfNode extends AstTernaryNode {

	public AstIfNode() {
	}

	public AstIfNode(AstNode condition, AstNode ifTrue, AstNode ifFalse) {
		this.condition = condition;
		this.left = ifTrue;
		this.right = ifFalse;
	}

	public AstNode getCondition() {
		return condition;
	}

	public void setCondition(AstNode condition) {
		this.condition = condition;
	}

	public AstNode getIfTrue() {
		return left;
	}

	public void setIfTrue(AstNode ifTrue) {
		this.left = ifTrue;
	}

	public AstNode getIfFalse() {
		return right;
	}

	public void setIfFalse(AstNode ifFalse) {
		this.right = ifFalse;
	}

	@Override
	public void run(Interpreter interpreter) {
		this.condition.run(interpreter);
		if ((Boolean) value() && this.getIfTrue() != null){
			this.getIfTrue().run(interpreter);
		} else if (getIfFalse() != null) {
			this.getIfFalse().run(interpreter);
		}
	}

	@Override
	public Object value() {
		return this.condition.value();
	}

	@Override
	public String toString() {
		return "AstIfNode{" +
				"condition=" + condition +
				'}';
	}
}
