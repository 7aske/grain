package com._7aske.grain.http.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.LexerException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataView extends AbstractView {
	private final String START_TAG = "<%";
	private final String OUT_TAG = "<%=";
	private final String END_TAG = "%>";
	private final Pattern VARIABLE_PATTERN = Pattern.compile("(?!(<!--).*?)<%=\\s*?([A-Za-z0-9-_$]+)\\s*?%>(?!.*?(-->))");
	private final Pattern CODE_SEGMENT = Pattern.compile("(?!(<!--).*?)<%[^=]\\s*?(.*?)\\s*?%>(?!.*?(-->))");
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
			String content = super.getContent();

			boolean hasCodeSegments = false;

			StringBuilder code = new StringBuilder();
			StringBuilder result = new StringBuilder();
			Matcher codeSegments = CODE_SEGMENT.matcher(content);

			MatchResult matchResult = null;
			while (codeSegments.find()) {
				hasCodeSegments = true;
				matchResult = codeSegments.toMatchResult();
				if (result.length() == 0)
					result.append(content, 0, codeSegments.start());

				String segment = codeSegments.group(2);

				if (isBlock(segment)) {
					code.append(" ").append(segment).append(" ");
					Matcher nextSegment = CODE_SEGMENT.matcher(content.substring(codeSegments.end()));
					if (!nextSegment.find())
						continue;
					String html = content.substring(codeSegments.end(), codeSegments.end() + nextSegment.start());
					int identifier = (int) Math.abs(Math.random() * html.hashCode()); // can produce collisions
					String varName = "r" + identifier;
					code.append(varName).append("=").append("'").append(html).append("'").append(";");
					result.append(OUT_TAG).append(varName).append(END_TAG);
				}
			}


			// if we had any code segments then it makes sense to run the interpreter
			if (hasCodeSegments) {
				result.append(content, matchResult.end(), content.length());
				Interpreter interpreter = new Interpreter(code.toString(), data);
				interpreter.run();

				cachedContent = substituteValues(result, interpreter.getSymbols());
			} else {
				// otherwise, we just substitute regular variables
				codeSegments.appendTail(result);
				cachedContent = substituteValues(result, data);
			}

		}

		return cachedContent;
	}

	// lexing the found block to see if there are any valid code tokens
	private boolean isBlock(String code) {
		try {
			return new Lexer(code).getTokens().size() > 2; // two tokens that are always there are _START and _END
		} catch (LexerException | NoSuchElementException ex) {
			return false;
		}
	}

	private String substituteValues(CharSequence content, Map<String, Object> data) {
		StringBuilder result = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(content);
		while (matcher.find()) {
			String key = matcher.group(2);
			if (key != null) {
				Object value = data.getOrDefault(key, "");
				matcher.appendReplacement(result, substituteValues(value == null ? "null" : value.toString(), data));
			}
		}

		matcher.appendTail(result);

		return result.toString();
	}
}
