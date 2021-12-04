package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

public class AstFragmentCallNode extends AstUnaryNode {

	private AstSymbolNode symbol;
	private List<AstNode> arguments;

	public AstFragmentCallNode() {
	}

	public AstSymbolNode getSymbol() {
		return symbol;
	}

	public List<AstNode> getArguments() {
		return arguments;
	}

	public void setSymbol(AstSymbolNode symbol) {
		this.symbol = symbol;
	}

	public void setArguments(List<AstNode> arguments) {
		this.arguments = arguments;
	}

	@Override
	public Object run(Interpreter interpreter) {
		AstFragmentNode fragmentNode = (AstFragmentNode) interpreter.getSymbolValue(this.symbol.getName());
		// Arguments setup
		Map<String, Object> argMap = new HashMap<>();
		argMap.put("arguments", this.arguments);
		for (AstNode arg : this.arguments) {
			if (!(arg instanceof AstKeywordArgumentNode)) {
				// we parse only the keyword arguments
				continue;
			}
			argMap.put(((AstKeywordArgumentNode) arg).getSymbol().getName(), ((AstKeywordArgumentNode) arg).getValue().run(interpreter));
		}

		// @Refactor this is probably very clunky and can be interpreted in
		// the current interpreter context but this was an easy way to make
		// this functionality work.
		String interpreted = Interpreter.interpret((String) fragmentNode.run(interpreter), argMap);
		interpreter.write(interpreted);
		return interpreted;
	}
}
