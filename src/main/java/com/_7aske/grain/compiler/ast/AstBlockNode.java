package com._7aske.grain.compiler.ast;

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
	public String toString() {
		return "AstBlockNode{" +
				"nodes=" + nodes.size() +
				'}';
	}
}
