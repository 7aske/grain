package com._7aske.grain.http.view;

import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataView extends AbstractView {
	private final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([A-Za-z-_$]+)}}");
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

		StringBuilder builder = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(content);

		StringBuilder code = new StringBuilder();

		int codeStart = content.indexOf("{{");
		while(codeStart != -1) {
			int codeEnd = content.indexOf("}}", codeStart + 2);
			if (codeEnd == -1)
				break;

			String segment = content.substring(codeStart + 2, codeEnd);
			code.append(" ").append(segment).append(" ");

			codeStart = content.indexOf("{{", codeStart + 2);
		}

		Lexer lexer = new Lexer(code.toString());
		lexer.begin();
		Parser parser = new Parser(lexer);
		parser.parse();


		// while (matcher.find()) {
		// 	String key = matcher.group(1);
		// 	if (key != null)
		// 		matcher.appendReplacement(builder, data.get(key));
		// }

		// matcher.appendTail(builder);

		return content;
	}
}
