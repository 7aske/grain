package com._7aske.grain.http.view;

import com._7aske.grain.compiler.ast.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com._7aske.grain.compiler.lexer.TokenType.*;

public class DataView extends AbstractView {
	private final Pattern VARIABLE_PATTERN = Pattern.compile("<%=([A-Za-z0-9-_$]+)%>");
	private final Pattern CODE_START_PATTERN = Pattern.compile("<%[^=]");
	private Map<String, String> data = null;

	public DataView(String path) {
		super(path);
	}

	public void setData(String key, String value) {
		if (data == null)
			data = new HashMap<>();
		data.put(key, value);
	}

	@Override
	public String getContent() {
		String content = super.getContent();
		if (data == null)
			return content;


		StringBuilder contentBuilder = new StringBuilder();
		StringBuilder code = new StringBuilder();

		Matcher codeStart = CODE_START_PATTERN.matcher(content);
		int codeEnd = -1;
		int resultCount = 0;
		if (codeStart.find()) {
			contentBuilder.append(content, 0, codeStart.start());
			while (true) {
				codeEnd = content.indexOf("%>", codeStart.start() + 2);
				if (codeEnd == -1)
					break;

				String segment = content.substring(codeStart.start() + 2, codeEnd);
				code.append(" ").append(segment).append(" ");

				if (codeStart.find()) {
					Lexer lexer = new Lexer(code.toString());
					lexer.begin();
					if (lexer.getTokens().size() > 0 && (lexer.getTokens().get(1).isOfType(IF, ELSE, ELIF))) {
						String html = content.substring(codeEnd + 2, codeStart.start());
						code.append("result").append(resultCount).append("=").append("print('").append(html).append("')");
						contentBuilder.append("<%=result").append(resultCount).append("%>");
						resultCount++;
					}
				} else {
					break;
				}
			}
			contentBuilder.append(content, codeEnd + 2, content.length());
		}

		Lexer lexer = new Lexer(code.toString());
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode root = parser.parse();
		Interpreter interpreter = new Interpreter();
		for (Map.Entry<String, String> kv : data.entrySet()) {
			interpreter.putSymbol(kv.getKey(), kv.getValue());
		}
		interpreter.addNode(root);
		interpreter.run();

		return substituteValues(contentBuilder, interpreter);
	}

	private String substituteValues(StringBuilder builder, Interpreter interpreter) {
		StringBuilder result = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(builder);
		while (matcher.find()) {
			String key = matcher.group(1);
			if (key != null) {
				String value = (String) interpreter.getSymbolValueOrDefault(key, "");
				matcher.appendReplacement(result, substituteValues(new StringBuilder(value), interpreter));
			}
		}

		matcher.appendTail(result);

		return result.toString();

	}
}
