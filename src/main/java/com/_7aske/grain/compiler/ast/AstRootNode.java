package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.List;

public class AstRootNode extends AstUnaryNode {
	private List<AstNode> nodes;

	public AstRootNode() {
		this(new ArrayList<>());
	}


	public AstRootNode(List<AstNode> nodes) {
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
		for (AstNode node : this.getNodes()) {
			node.run(interpreter);
		}
	}

	@Override
	public Object value() {
		if (nodes.isEmpty()) return null;
		return nodes.get(nodes.size() - 1).value();
	}
}
