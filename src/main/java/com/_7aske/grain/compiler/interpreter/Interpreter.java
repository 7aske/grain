package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstBlockNode;
import com._7aske.grain.compiler.ast.AstFunctionCallNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.exception.InterpreterException;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;
import com._7aske.grain.compiler.util.AstUtil;
import com._7aske.grain.util.formatter.StringFormat;

import java.util.*;
import java.util.stream.Collectors;

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

	public void putProperties(Properties properties) {

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			Object k = entry.getKey();
			Object v = entry.getValue();
			String key = (String) k;
			String[] parts = key.split("\\.");
			Map<String, Object> lastMap = null;
			Map<String, Object> firstMap = null;
			if (parts.length > 1)
				for (int i = 0; i < parts.length - 1; i++) {
					String part = parts[i];

					// Handling actual data in the symbol table
					// The problem here is merging existing data otherwise
					// this would be a simple loop. We always need to check
					// whether the last reference contains the current key.
					// If it does then we update but if not we crate a new
					// HashMap with empty values.
					Map<String, Object> ref;
					if (lastMap == null) {
						// first iteration
						Object existing = getSymbolValue(part);

						if (existing != null && !Map.class.isAssignableFrom(existing.getClass()))
							throw new InterpreterException(StringFormat.format("Symbol '{}' already defined and is of type '{}'", part, existing.getClass()));

						firstMap = existing == null ? new HashMap<>() : (Map<String, Object>) existing;
						ref = firstMap;
					} else {
						if (lastMap.containsKey(part)) {
							Object existing = getSymbolValue(part);

							if (existing != null && !Map.class.isAssignableFrom(existing.getClass()))
								throw new InterpreterException(StringFormat.format("Symbol '{}' already defined and is of type '{}'", part, existing.getClass()));
							ref = (Map<String, Object>) lastMap.get(part);
						} else {
							ref = new HashMap<>();
							lastMap.put(part, ref);
						}
					}
					lastMap = ref;
				}
			if (lastMap == null) {
				lastMap = new HashMap<>();
			}
			// In the end we re-wire the maps to their corresponding symbols
			lastMap.put(parts[parts.length - 1], v);
			putSymbol(parts[0], firstMap);
		}
	}

	public void putSymbols(Map<String, Object> data) {
		scopeStack.getFirst().putAll(data);
	}

	public void putSymbol(String name, Object value) {
		scopeStack.getFirst().put(name, value);
	}

	public void putScopedSymbol(String name, Object value) {
		Map<String, Object> scope = getScopeThatContains(name);
		scope.put(name, value);
	}

	public void pushScope() {
		this.scopeStack.push(new HashMap<>());
	}

	public void popScope() {
		this.scopeStack.pop();
	}

	public Object getSymbolValue(String name) {
		Object o = null;
		Map<String, Object> scope = getScopeThatContains(name);

		if (scope.containsKey(name)) {
			o = scope.get(name);
		} else {
			Optional<Class<?>> clazz = tryLoadClass("java.lang." + name.replaceAll("java.lang.", ""));
			if (clazz.isPresent()) {
				o = clazz.get();
				putScopedSymbol(name, o);
			}
		}

		return o;
	}

	private Map<String, Object> getScopeThatContains(String name) {
		return scopeStack.stream()
				.filter(s -> s.containsKey(name))
				.findFirst().orElse(scopeStack.getFirst());
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

	public Object evaluate(String code) {
		this.nodes.clear();
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		addNode(parser.parse());
		Object value = null;
		for (AstNode node : this.nodes) {
			try {
				value = node.run(this);
			} catch (InterpreterException ignored) {
				value = null;
			}
		}
		return value;
	}

	public boolean analyze(String code) {
		this.nodes.clear();
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstNode root = parser.parse();
		List<String> symbolsNames = new ArrayList<>();
		AstUtil.getSymbols(root, symbolsNames);
		return symbolsNames.stream().filter(s -> {
			if (this.getSymbols().containsKey(s)) {
				Object val = this.getSymbols().get(s);
				return val != null && Map.class.isAssignableFrom(val.getClass());
			}
			return false;
		}).count() == symbolsNames.size();
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
