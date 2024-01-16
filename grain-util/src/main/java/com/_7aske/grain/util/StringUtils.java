package com._7aske.grain.util;

import java.util.Locale;

public class StringUtils {
	private StringUtils() {}
	public static String camelToSnake(String str) {
		if (str == null) return null;
		if (str.length() == 1) return str.toLowerCase(Locale.ROOT);
		StringBuilder builder = new StringBuilder();

		boolean lastWasUpper = true;
		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char curr = arr[i];
			if (Character.isLowerCase(curr)) {
				builder.append(curr);
				lastWasUpper = false;
			} else {
				if (!lastWasUpper) {
					builder.append("_");
				}
				builder.append(Character.toLowerCase(curr));
				lastWasUpper = true;
			}
		}

		return builder.toString();
	}

    public static boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 == null || str2 == null) return false;

		return str1.toLowerCase(Locale.ROOT).equals(str2.toLowerCase(Locale.ROOT));
    }

    public static boolean isBlank(String queryString) {
		return queryString == null || queryString.isBlank();
    }

    public static String stripQuotes(String paramPart) {
		return paramPart.replaceAll("^\"", "")
				.replaceAll("\"$", "");
    }
}
