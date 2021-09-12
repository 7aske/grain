package com._7aske.grain.http.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.LexerException;
import com._7aske.grain.compiler.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com._7aske.grain.compiler.lexer.TokenType.*;

public class DataView extends AbstractView {
	private final String START_TAG = "<%";
	private final String OUT_TAG = "<%=";
	private final String END_TAG = "%>";
	private final Pattern VARIABLE_PATTERN = Pattern.compile("<%=([A-Za-z0-9-_$]+)%>");
	private final Pattern CODE_SEGMENT = Pattern.compile("(?!(<!--).*?)<%[^=](.*?)%>(?!.*?(-->))");
	private Map<String, String> data = null;
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

			StringBuilder code = new StringBuilder();
			StringBuilder result = new StringBuilder();
			Matcher codeSegments = CODE_SEGMENT.matcher(content);

			MatchResult matchResult = null;
			while (codeSegments.find()) {
				matchResult = codeSegments.toMatchResult();
				if (result.length() == 0)
					result.append(content, 0, codeSegments.start());

				String segment = codeSegments.group(2);
				code.append(" ").append(segment).append(" ");

				if (isBlock(segment)) {
					Matcher nextSegment = CODE_SEGMENT.matcher(content.substring(codeSegments.end()));
					if (!nextSegment.find())
						continue;
					String html = content.substring(codeSegments.end(), codeSegments.end() + nextSegment.start());
					int identifier = (int) Math.abs(Math.random() * html.hashCode()); // can produce collisions
					String varName = "r" + identifier;
					code.append(varName).append("=").append("'").append(html).append("'");
					result.append(OUT_TAG).append(varName).append(END_TAG);
				}
			}

			if (matchResult == null)
				result.append(content);
			else
				result.append(content, matchResult.end(), content.length());

			Lexer lexer = new Lexer(code.toString());
			Parser parser = new Parser(lexer);
			Interpreter interpreter = new Interpreter(parser);

			if (data != null) {
				for (Map.Entry<String, String> kv : data.entrySet()) {
					interpreter.putSymbol(kv.getKey(), kv.getValue());
				}
			}

			interpreter.run();

			cachedContent = substituteValues(result, interpreter);
		}

		return cachedContent;
	}

	private boolean isBlock(String code) {
		try {
			Lexer lexer = new Lexer(code);
			lexer.begin();
			return lexer.getTokens().size() > 0 && (lexer.getTokens().get(1).isOfType(IF, ELSE, ELIF));
		} catch (LexerException | NoSuchElementException ex) {
			return false;
		}
	}

	private String substituteValues(CharSequence content, Interpreter interpreter) {
		StringBuilder result = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(content);
		while (matcher.find()) {
			String key = matcher.group(1);
			if (key != null) {
				String value = (String) interpreter.getSymbolValueOrDefault(key, "");
				matcher.appendReplacement(result, substituteValues(value, interpreter));
			}
		}

		matcher.appendTail(result);

		return result.toString();

	}
}
