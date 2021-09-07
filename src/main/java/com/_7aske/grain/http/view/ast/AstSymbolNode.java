package com._7aske.grain.http.view.ast;

public class AstSymbolNode extends AstNode {
	String name;

	public AstSymbolNode() {
	}

	public AstSymbolNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "AstSymbolNode{" +
				"name='" + name + '\'' +
				'}';
	}
}
