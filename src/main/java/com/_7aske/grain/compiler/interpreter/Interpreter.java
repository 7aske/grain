package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstBlockNode;
import com._7aske.grain.compiler.ast.AstFragmentNode;
import com._7aske.grain.compiler.ast.AstFunctionCallback;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.exception.InterpreterException;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.LexerException;
import com._7aske.grain.compiler.parser.Parser;
import com._7aske.grain.compiler.util.AstUtil;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.formatter.StringFormat;
import com._7aske.grain.web.view.FileView;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.regex.Pattern.compile;

public class Interpreter {
	public static final Pattern VARIABLE_PATTERN = compile("<%=\\s*?(.*?)\\s*?%>");
	public static final Pattern INTERPOLATION_PATTERN = compile("\\$\\{\\s*?(.*?)\\s*?}");
	public static final Pattern CODE_SEGMENT = compile("<%[^=]\\s*?(.*?)\\s*?%>");
	public static final Pattern COMMENT_PATTERN = compile("((<[%!]--).*?(--%?>))");
	private final List<AstNode> nodes;
	private final InterpreterOutput output;
	private final Deque<Map<String, Object>> scopeStack;
	private static final Logger log = LoggerFactory.getLogger(Interpreter.class);

	public Interpreter() {
		this.nodes = new ArrayList<>();
		this.scopeStack = new ArrayDeque<>();
		this.scopeStack.push(new HashMap<>());
		this.output = new InterpreterOutput();
		this.scopeStack.getFirst().put("print", (AstFunctionCallback) (args) -> {
			String value = (args[0] == null ? "null" : args[0].toString());
			value = value.replaceAll("\\\\'", "'").replaceAll("\\\\\"", "\"");
			write(value);
			return value;
		});
		this.scopeStack.getFirst().put("println", (AstFunctionCallback) (args) -> {
			String value = (args[0] == null ? "null" : args[0].toString()) + "<br/>";
			value = value.replaceAll("\\\\'", "'").replaceAll("\\\\\"", "\"");
			write(value);
			return value;
		});

		this.scopeStack.getFirst().put("printf", (AstFunctionCallback) (args) -> {
			String value = String.format((String) args[0], Arrays.copyOfRange(args, 1, args.length));
			value = value.replaceAll("\\\\'", "'").replaceAll("\\\\\"", "\"");
			write(value);
			return value;
		});

		this.scopeStack.getFirst().put("range", (AstFunctionCallback) (args) -> {
			int value = Integer.parseInt(args[0].toString());
			return IntStream.range(0, value).boxed().collect(Collectors.toList());
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
			if (parts.length > 1) {
				for (int i = 0; i < parts.length - 1; i++) {
					String part = parts[i];

					// Handling actual data in the symbol table
					// The problem here is merging existing data otherwise
					// this would be a simple loop. We always need to check
					// whether the last reference contains the current key.
					// If it does then we update but if not we crate a new
					// HashMap with empty values.
					Map<String, Object> ref = null;
					if (lastMap == null) {
						// first iteration
						Object existing = getSymbolValue(part);

						if (existing != null && !Map.class.isAssignableFrom(existing.getClass())) {
							log.error("Symbol '{}' already defined", parts[parts.length - 1]);
							// throw new InterpreterException(StringFormat.format("Symbol '{}' already defined and is of type '{}'", part, existing.getClass()));
						} else {
							firstMap = existing == null ? new HashMap<>() : (Map<String, Object>) existing;
							ref = firstMap;
						}
					} else {
						if (lastMap.containsKey(part)) {
							Object existing = lastMap.get(part);

							if (existing != null && !Map.class.isAssignableFrom(existing.getClass())) {
								log.error("Symbol '{}' already defined", parts[parts.length - 1]);
							} else {
								// throw new InterpreterException(StringFormat.format("Symbol '{}' already defined and is of type '{}'", part, existing.getClass()));
								ref = existing == null ? new HashMap<>() : (Map<String, Object>) existing;
							}
						} else {
							ref = new HashMap<>();
							lastMap.put(part, ref);
						}
					}
					lastMap = ref;
				}
			}
			if (lastMap == null) {
				lastMap = new HashMap<>();
			}

			// @Refactor Feels like there are many checks like these.
			// @Warning We're for now ignoring errors like these because we want
			// to be able to load environment variables. This should throw in cases
			// where we have a key like: "java.home:/var/lib/jvm" and "java.home.path:/var/lib/jvm".
			// We shouldn't be able to create a property "path" on a object that
			// already has a string value. Since this situations are common when
			// it comes to env variables we just simply ignore them.
			if (lastMap.containsKey(parts[parts.length - 1])) {
				log.error("Symbol '{}' already defined", parts[parts.length - 1]);
				// throw new InterpreterException(StringFormat.format("Symbol '{}' already defined", parts[parts.length - 1]));
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

	public void putCurrentScopeSymbol(String name, Object value) {
		Map<String, Object> scope = this.scopeStack.peek();
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
			} catch (InterpreterException e) {
				e.printStackTrace();
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

	public Optional<AstFragmentNode> tryIncludeFragment(String path) {
		if (path == null)
			return Optional.empty();
		FileView fileView = new FileView(path);
		if (fileView.getContent().isEmpty())
			return Optional.empty();

		return Optional.of(new AstFragmentNode(fileView));
	}

	public Map<String, Object> getSymbols() {
		return this.scopeStack.getFirst();
	}

	public static String interpret(String rawTemplate, Map<String, Object> data) {
		String content = rawTemplate
				.replaceAll("\n", "")
				.replaceAll(COMMENT_PATTERN.pattern(), "");

		StringBuilder preCode = new StringBuilder();
		Matcher variableSegments = VARIABLE_PATTERN.matcher(content);
		while (variableSegments.find()) {
			variableSegments.appendReplacement(preCode, StringFormat.format("<% print({}); %>", variableSegments.group(1)));
		}
		variableSegments.appendTail(preCode);

		// @Optimization probably we don't need to do two passes
		Matcher interpolationSegments = INTERPOLATION_PATTERN.matcher(preCode.toString());
		preCode = new StringBuilder();
		while (interpolationSegments.find()) {
			interpolationSegments.appendReplacement(preCode, StringFormat.format("<% print({}); %>", interpolationSegments.group(1)));
		}
		interpolationSegments.appendTail(preCode);


		content = preCode.toString();
		StringBuilder code = new StringBuilder();
		Matcher codeSegments = CODE_SEGMENT.matcher(content);

		MatchResult matchResult = null;
		while (codeSegments.find()) {
			matchResult = codeSegments.toMatchResult();
			if (code.length() == 0)
				code.append(createPrintStatement(content, 0, codeSegments.start()));

			String segment = codeSegments.group(1);

			if (isBlock(segment)) {
				code.append(" ").append(segment).append(" ");
				Matcher nextSegment = CODE_SEGMENT.matcher(content.substring(codeSegments.end()));
				if (!nextSegment.find())
					continue;
				code.append(createPrintStatement(content, codeSegments.end(), codeSegments.end() + nextSegment.start()));
			}
		}

		if (matchResult == null) {
			return substituteValues(content, data);
		} else {
			code.append(createPrintStatement(content, matchResult.end(), content.length()));
			Interpreter interpreter = new Interpreter(code.toString(), data);
			interpreter.run();
			return substituteValues(interpreter.getContent(), interpreter.getSymbols());
		}
	}

	private static String createPrintStatement(String content, int start, int end) {
		return createPrintStatement(content.substring(start, end));
	}

	private static String createPrintStatement(String content) {
		return String.format("print('%s');", content.replaceAll("'", "\\\\'"));
	}

	// lexing the found block to see if there are any valid code tokens
	private static boolean isBlock(String code) {
		try {
			return new Lexer(code).getTokens().size() > 2; // two tokens that are always there are _START and _END
		} catch (LexerException | NoSuchElementException ex) {
			return false;
		}
	}

	private static Object tryInterpret(String code, Map<String, Object> symbols) {
		return new Interpreter(code, symbols).run();
	}

	private static String substituteValues(CharSequence content, Map<String, Object> symbols) {
		StringBuilder result = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(content);
		while (matcher.find()) {
			String key = matcher.group(1);
			if (key != null) {
				Object value = tryInterpret(key, symbols);
				matcher.appendReplacement(result, value == null ? "null" : value.toString());
			}
		}

		matcher.appendTail(result);

		return result.toString();
	}
}
