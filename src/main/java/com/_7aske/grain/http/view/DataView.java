package com._7aske.grain.http.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.LexerException;

import java.nio.channels.AsynchronousByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

public class DataView extends AbstractView {
	private final Pattern VARIABLE_PATTERN = compile("<%=\\s*?(.*?)\\s*?%>");
	private final Pattern CODE_SEGMENT = compile("<%[^=]\\s*?(.*?)\\s*?%>");
	private final Pattern COMMENT_PATTERN = compile("((<!--).*?(-->))");
	private Map<String, Object> data = null;
	private String cachedContent = null;

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
		if (cachedContent == null) {
			String content = super.getContent()
					.replaceAll("\n", "")
					.replaceAll(COMMENT_PATTERN.pattern(), "");

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
				cachedContent = substituteValues(content, data);
			} else {
				code.append(createPrintStatement(content, matchResult.end(), content.length()));
				Interpreter interpreter = new Interpreter(code.toString(), data);
				interpreter.run();
				cachedContent = substituteValues(interpreter.getContent(), interpreter.getSymbols());
			}
		}

		return cachedContent;
	}

	private String createPrintStatement(String content, int start, int end) {
		return String.format("print('%s');", content.substring(start, end).replaceAll("'", "\\\\'"));
	}

	private String createPrintStatement(String content) {
		return String.format("print('%s');", content.replaceAll("'", "\\\\'"));
	}

	// lexing the found block to see if there are any valid code tokens
	private boolean isBlock(String code) {
		try {
			return new Lexer(code).getTokens().size() > 2; // two tokens that are always there are _START and _END
		} catch (LexerException | NoSuchElementException ex) {
			return false;
		}
	}

	private Object tryInterpret(String code, Map<String, Object> symbols) {
		return new Interpreter(code, symbols).run();
	}

	private String substituteValues(CharSequence content, Map<String, Object> symbols) {
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
