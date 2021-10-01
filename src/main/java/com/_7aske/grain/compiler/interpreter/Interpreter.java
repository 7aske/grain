package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstBlockNode;
import com._7aske.grain.compiler.ast.AstFunctionCallNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;

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
			String value = (args[0] == null ? "null" : args[0].toString());
			write(value);
			return value;
		});
		this.scopeStack.getFirst().put("println", (AstFunctionCallNode.AstFunctionCallback) (args) -> {
			String value = (args[0] == null ? "null" : args[0].toString()) + "<br/>";
			write(value);
			return value;
		});

		this.scopeStack.getFirst().put("printf", (AstFunctionCallNode.AstFunctionCallback) (args) -> {
			String value = String.format((String) args[0], Arrays.copyOfRange(args, 1, args.length));
			write(value);
			return value;
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

	public void putScopedSymbol(String symbol, Object value) {
		Map<String, Object> scope = scopeStack.stream()
				.filter(s -> s.containsKey(symbol))
				.findFirst().orElse(scopeStack.peek());
		// scope stack cannot be empty (global scope)
		scope.put(symbol, value);
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
				.findFirst().orElse(scopeStack.getFirst());

		if (scope.containsKey(symbolName)) {
			o = scope.get(symbolName);
		} else {
			Optional<Class<?>> clazz = tryLoadClass("java.lang." + symbolName.replaceAll("java.lang.", ""));
			if (clazz.isPresent()) {
				o = clazz.get();
				putScopedSymbol(symbolName, o);
			}
		}

		return o;
	}

	public String getContent() {
		return output.getContent();
	}

	public Object run() {
		Object value = null;
		for (AstNode node : this.nodes) {
			value = node.run(this);
		}
		return value;
	}

	Optional<Class<?>> tryLoadClass(String classPath) {
		try {
			return Optional.of(ClassLoader.getSystemClassLoader().loadClass(classPath));
		} catch (ClassNotFoundException e) {
			return Optional.empty();
		}
	}

	public Map<String, Object> getSymbols() {
		return this.scopeStack.getFirst();
	}
}
