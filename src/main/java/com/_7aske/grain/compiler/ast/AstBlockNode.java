package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.List;

public class AstBlockNode extends AstNode {
	private List<AstNode> nodes;

	public AstBlockNode() {
		this(new ArrayList<>());
	}


	public AstBlockNode(List<AstNode> nodes) {
		this.nodes = nodes;
	}

	public void addNode(AstNode node) {
		this.nodes.add(node);
	}

	public List<AstNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<AstNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void run(Interpreter interpreter) {
		for (AstNode node: this.getNodes()) {
			node.run(interpreter);
		}
	}

	@Override
	public Object value() {
		throw new RuntimeException("Called 'value()' on " + this.getClass());
	}
}
