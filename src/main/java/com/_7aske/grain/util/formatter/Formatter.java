package com._7aske.grain.util.formatter;

import java.util.NoSuchElementException;

// @Incomplete missing decimal format
public class Formatter {
	private final String format;

	public Formatter(String format) {
		this.format = format;
	}

	public String format(Object... params) {
		StringBuilder builder = new StringBuilder();
		FormatIterator iter = new FormatIterator(this.format);
		int currParam = 0;
		try {
			while (iter.hasNext()) {
				char next = iter.next();
				int padLength = 0;
				boolean padRight = false;
				int precision = 0;
				if (next == '{' && !iter.isPeek('{')) {
					Object toPrint;
					if (Character.isDigit(iter.peek())) {
						int index = Integer.parseInt(iter.eatWhile(Character::isDigit));
						toPrint = params[index];
					} else {
						toPrint = params[currParam++];
					}
					String toPrintStr = toPrint == null ? "null" : toPrint.toString();
					if (iter.isPeek(':')) {
						iter.next();
						if (iter.isPeek('-')) {
							padRight = true;
							iter.next(); // skip -
						}
						if (Character.isDigit(iter.peek())) {
							padLength = Integer.parseInt(iter.eatWhile(Character::isDigit));
						}
					}
					if (iter.isPeek('.')) {
						iter.next(); // skip .
						precision = Integer.parseInt(iter.eatWhile(Character::isDigit));
						// @Temporary do proper formatting of floats
						toPrintStr = String.format("%."+precision+"f", toPrint);
					}
					int len = toPrintStr.length();
					// @Temporary nasty hack to remove ANSI escape chars
					// used for colored output
					if (toPrintStr.startsWith("\u001B")) {
						len -= 9;
					}
					String pad = " ".repeat(Math.max(0, padLength - len));
					if (!padRight)
						builder.append(pad);
					builder.append(toPrintStr);
					if (padRight)
						builder.append(pad);
					if (iter.isPeek('}'))
						iter.next(); // skip ending }
				} else {
					builder.append(next);
				}
			}

			return builder.toString();
		} catch (NoSuchElementException e) {
			return builder.toString();
		}
	}
}
