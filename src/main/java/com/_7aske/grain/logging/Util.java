package com._7aske.grain.logging;

/**
 * Utility class used in logging classes.
 */
public class Util {
	private Util() {
	}

	/**
	 * Shortens the FQCN to fit the given length.
	 *
	 * E.g. 'c._.g.compiler.interpreter.Interpreter' will be shortened to
	 * 'c._.g.compiler.interpreter.Interpreter'.
	 *
	 * @param className the FQCN to shorten
	 * @param length the length to fit the FQCN to
	 * @return the shortened FQCN
	 */
	public static String shortenClassName(String className, int length) {
		if (className.length() <= length) {
			return className;
		}

		String[] parts = className.split("\\.");

		if (parts[parts.length - 2].length() == 1) {
			String joined = String.join(".", parts);
			if (joined.length() > length) {
				return joined.substring(joined.length() - length);
			}
			return joined;
		}

		for (int i = 0; i < parts.length; i++) {
			if (parts[i].length() != 1) {
				parts[i] = parts[i].substring(0, 1);
				break;
			}
		}

		return shortenClassName(String.join(".", parts), length);
	}

}
