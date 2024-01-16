package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.ast.basic.AstUnaryNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

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
	public Object run(Interpreter interpreter) {
		Object value = null;
		for (AstNode node : this.getNodes()) {
			value = node.run(interpreter);
		}
		return value;
	}
}
