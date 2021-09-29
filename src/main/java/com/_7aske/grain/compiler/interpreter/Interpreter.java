package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstBlockNode;
import com._7aske.grain.compiler.ast.AstFunctionCallNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Interpreter {
	private final List<AstNode> nodes;
	private final InterpreterOutput output;
	private final Deque<Map<String, Object>> scopeStack;

	public Interpreter() {
		this.nodes = new ArrayList<>();
		this.scopeStack = new ArrayDeque<>();
		this.scopeStack.push(new HashMap<>());
		this.output = new InterpreterOutput();
		this.scopeStack.getFirst().put("print", (AstFunctionCallNode.AstFunctionCallback) (args) -> {
			write(args[0].toString());
			return null;
		});
		this.scopeStack.getFirst().put("println", (AstFunctionCallNode.AstFunctionCallback) (args) -> {
			write(args[0].toString());
			write("<br/>");
			return null;
		});
	}

	public Interpreter(String code, Map<String, Object> symbols) {
		this();
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		if (symbols != null)
			putSymbols(symbols);
		addNode(parser.parse());
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

	public void write(CharSequence text) {
		output.write(text);
	}

	public void putSymbols(Map<String, Object> data) {
		scopeStack.getFirst().putAll(data);
	}

	public void putSymbol(String data, Object value) {
		scopeStack.getFirst().put(data, value);
	}

	public void putScopedSymbol(String data, Object value) {
		Map<String, Object> scope = scopeStack.stream()
				.filter(s -> s.containsKey(data))
				.findFirst().orElse(scopeStack.peek());
		scope.put(data, value);
	}

	public void pushScope() {
		this.scopeStack.push(new HashMap<>());
	}

	public void popScope() {
		this.scopeStack.pop();
	}

	public Object getSymbolValue(String symbolName) {
		Object o = null;
		Map<String, Object> scope = scopeStack.stream()
				.filter(s -> s.containsKey(symbolName))
				.findFirst().orElse(null);
		if (scope != null && scope.containsKey(symbolName)) {
			o = scope.get(symbolName);
		}

		// if (o == null) {
		// 	String[] parts = symbolName.split("\\.");
		// 	String className = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
		// 	String methodName = parts[parts.length - 1];
		// 	try {
		// 		Class<?> clazz = getClass().getClassLoader().loadClass(className);
		// 		AstFunctionCallNode.AstFunctionCallback callback = (args) -> {
		// 			try {
		// 				Method method = clazz.getMethod(methodName, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
		// 				return method.invoke(null, args);
		// 			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
		// 				throw new IllegalArgumentException();
		// 			}
		// 		};
		// 		scopeStack.getFirst().put(symbolName, callback);
		// 		return callback;
		// 	} catch (ClassNotFoundException ex) {
		// 		return null;
		// 	}
		// }
		return o;
	}

	public String getContent() {
		return output.getContent();
	}

	public Object run() {
		for (AstNode node : this.nodes) {
			node.run(this);
		}
		if (nodes.isEmpty()) return null;
		return nodes.get(nodes.size() - 1).value();
	}

	public Map<String, Object> getSymbols() {
		return this.scopeStack.getFirst();
	}
}
