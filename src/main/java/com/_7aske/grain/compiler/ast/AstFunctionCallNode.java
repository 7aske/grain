package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.List;

public class AstFunctionCallNode extends AstUnaryNode {
	@FunctionalInterface
	public interface AstFunctionCallback {
		Object call(Object... args);
	}

	private AstSymbolNode name;
	private List<AstNode> arguments;
	private AstFunctionCallback callback;

	public AstFunctionCallNode() {
	}

	public AstSymbolNode getName() {
		return name;
	}

	public List<AstNode> getArguments() {
		return arguments;
	}

	public void setName(AstSymbolNode name) {
		this.name = name;
	}

	public void setArguments(List<AstNode> arguments) {
		this.arguments = arguments;
	}

	public AstFunctionCallback getCallback() {
		return callback;
	}

	public void setCallback(AstFunctionCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run(Interpreter interpreter) {
		for (AstNode arg : arguments) {
			arg.run(interpreter);
		}
		this.callback = (AstFunctionCallback) interpreter.getSymbolValue(this.name.name);
	}

	@Override
	public Object value() {
		return callback.call(arguments.stream().map(AstNode::value).toArray(Object[]::new));
	}

}
