package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.interpreter.exception.InterpreterNoSuchMethodException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

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
		fragmentNode.setArguments(this.getArguments());
		String fragmentContent = (String) fragmentNode.run(interpreter);
		interpreter.write(fragmentContent);
		return fragmentContent;
	}
}
