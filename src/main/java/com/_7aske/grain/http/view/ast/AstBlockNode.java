package com._7aske.grain.http.view.ast;

import java.util.List;

public class AstBlockNode extends AstNode {
	private List<AstNode> nodes;
	public AstBlockNode() {
	}

	public AstBlockNode(List<AstNode> nodes) {
		this.nodes = nodes;
	}

	public List<AstNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<AstNode> nodes) {
		this.nodes = nodes;
	}
}
