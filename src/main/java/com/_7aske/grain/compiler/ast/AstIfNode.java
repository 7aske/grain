package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstTernaryNode;
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
			interpreter.pushScope();
			this.getIfTrue().run(interpreter);
			interpreter.popScope();
		} else if (getIfFalse() != null) {
			interpreter.pushScope();
			this.getIfFalse().run(interpreter);
			interpreter.popScope();
		}
	}

	@Override
	public Object value() {
		return this.condition.value();
	}

}
