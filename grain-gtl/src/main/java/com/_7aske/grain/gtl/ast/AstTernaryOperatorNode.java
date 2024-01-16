package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.ast.basic.AstTernaryNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

import static com._7aske.grain.gtl.util.AstUtil.isFalsy;

public class AstTernaryOperatorNode extends AstTernaryNode {
	public static final int PRECEDENCE = 5000;

	public AstTernaryOperatorNode() {
	}

	public AstTernaryOperatorNode(AstNode condition, AstNode ifTrue, AstNode ifFalse) {
		this.condition = condition;
		this.left = ifTrue;
		this.right = ifFalse;
	}

	@Override
	public int getPrecedence() {
		return PRECEDENCE;
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
	public Object run(Interpreter interpreter) {
		Object value = null;
		Object conditionValue = this.condition.run(interpreter);
		if (!isFalsy(conditionValue) && this.getIfTrue() != null){
			value = this.getIfTrue().run(interpreter);
		} else if (getIfFalse() != null) {
			value = this.getIfFalse().run(interpreter);
		}
		return value;
	}
}
