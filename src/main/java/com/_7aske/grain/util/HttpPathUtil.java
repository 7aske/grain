package com._7aske.grain.util;


public class HttpPathUtil {
	public static final char PATH_SEP = '/';
	private HttpPathUtil() {
	}

	public static boolean arePathsMatching(String httpPath, String controllerPath) {
		String[] controllerPathSegments = trimFront(controllerPath, "/").split("/+");
		String[] pathSegments = trimFront(httpPath, "/").split("/+");
		if (pathSegments.length < controllerPathSegments.length) {
			return false;
		}
		for (int i = 0; i < controllerPathSegments.length; ++i) {
			if (!controllerPathSegments[i].equals(pathSegments[i])) {
				return false;
			}
		}
		return true;
	}

	public static String join(String... segments) {
		if (segments.length == 0) return "";
		if (segments.length == 1) return segments[0];

		StringBuilder builder = new StringBuilder(segments[0]);
		for (int i = 1; i < segments.length; i++) {
			String curr = segments[i];
			if (builder.charAt(builder.length() - 1) == PATH_SEP && curr.startsWith(String.valueOf(PATH_SEP))) {
				builder.append(curr.substring(1));
			} else if (builder.charAt(builder.length() - 1) != PATH_SEP && !curr.startsWith(String.valueOf(PATH_SEP))) {
				builder.append("/");
				builder.append(curr);
			} else {
				builder.append(curr);
			}
		}

		return builder.toString();
	}

	public static String trimFront(String string, String val) {
		if (val.length() == 0)
			throw new IllegalArgumentException("'val' cannot be an empty string");

		String copy = string;
		while (copy.startsWith(val))
			copy = copy.substring(val.length());

		return copy;
	}
}
