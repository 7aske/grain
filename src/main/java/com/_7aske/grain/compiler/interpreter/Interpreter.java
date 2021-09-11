package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstBlockNode;
import com._7aske.grain.compiler.ast.AstNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
	private final Map<String, Object> symbols;
	private final List<AstNode> nodes;

	public Interpreter() {
		this.symbols = new HashMap<>();
		this.nodes = new ArrayList<>();
	}

	public void addNode(AstNode node) {
		if (node instanceof AstBlockNode) {
			this.nodes.addAll(((AstBlockNode) node).getNodes());
		} else {
			this.nodes.add(node);
		}
	}

	public void putSymbols(HashMap<String, Object> data) {
		symbols.putAll(data);
	}

	public void putSymbol(String data, Object value) {
		symbols.put(data, value);
	}

	public Object getSymbolValue(String symbolName) {
		return symbols.get(symbolName);
	}

	public void run() {
		for (AstNode node : this.nodes) {
			node.run(this);
		}
	}

	public Map<String, Object> getSymbols() {
		return this.symbols;
	}
}
