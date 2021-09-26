package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstBlockNode;
import com._7aske.grain.compiler.ast.AstFunctionCallNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.parser.Parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Interpreter {
	private final Map<String, Object> symbols;
	private final List<AstNode> nodes;

	public Interpreter() {
		this.symbols = new HashMap<>();
		this.nodes = new ArrayList<>();
	}

	public Interpreter(Parser parser) {
		this();
		this.nodes.add(parser.parse());
	}

	public void addNode(AstNode node) {
		if (node instanceof AstBlockNode) {
			this.nodes.addAll(((AstBlockNode) node).getNodes());
		} else {
			this.nodes.add(node);
		}
	}

	public void putSymbols(Map<String, Object> data) {
		symbols.putAll(data);
	}

	public void putSymbol(String data, Object value) {
		symbols.put(data, value);
	}

	public Object getSymbolValue(String symbolName) {
		Object o = symbols.get(symbolName);
		if (o == null) {
			String[] parts = symbolName.split("\\.");
			String className = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
			String methodName = parts[parts.length - 1];
			try {
				Class<?> clazz = getClass().getClassLoader().loadClass(className);
				AstFunctionCallNode.AstFunctionCallback callback = (args) -> {
					try {
						Method method = clazz.getMethod(methodName, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
						return method.invoke(null, args);
					} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
						throw new IllegalArgumentException();
					}
				};
				symbols.put(symbolName, callback);
				return callback;
			} catch (ClassNotFoundException ex) {
				return null;
			}
		}
		return o;
	}

	public Object getSymbolValueOrDefault(String symbolName, Object def) {
		return symbols.getOrDefault(symbolName, def);
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
