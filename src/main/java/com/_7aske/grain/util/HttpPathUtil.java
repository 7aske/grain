package com._7aske.grain.util;


import com._7aske.grain.web.controller.annotation.PathVariable;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpPathUtil {
	public final static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("(\\{([\\w_]*(?=[\\w]+)[\\w\\d_]+)})", Pattern.MULTILINE);
	public static final char PATH_SEP = '/';
	public static final String SEGMENT_WILDCARD = "*";
	public static final String MULTIPLE_SEGMENT_WILDCARD = "**";

	private HttpPathUtil() {
	}

	/**
	 * Matches request paths in Ant format
	 * @param pattern pattern to match
	 * @param path request path to match the pattern to
	 * @return whether path matches pattern
	 */
	public static boolean antMatching(String pattern, String path) {
		String[] patternSegments = trimFront(pattern, "/").split("/+");
		String[] pathSegments = trimFront(path, "/").split("/+");

		int len = patternSegments.length;
		int pathLen = pathSegments.length;
		if (len == 1 && pathLen == 1 && patternSegments[0].equals(pathSegments[0])) return true;
		if (len > pathLen) return false;
		for (int i = 0, ip = 0; i < len && ip < pathLen; i++, ip++) {
			String patternSegment = patternSegments[i];
			String pathSegment = pathSegments[ip];
			if (!Objects.equals(patternSegment, pathSegment) &&
					(!patternSegment.equals(SEGMENT_WILDCARD) && !patternSegment.equals(MULTIPLE_SEGMENT_WILDCARD))) {
				return false;
			}

			if (patternSegment.equals(MULTIPLE_SEGMENT_WILDCARD)) {
				// If wildcard is the last segment
				if (i + 1 >= len) return true;
				String next = patternSegments[i + 1];
				int index = Arrays.asList(Arrays.copyOfRange(pathSegments, ip, pathSegments.length)).indexOf(next);
				if (index == -1) {
					return false;
				}
				i += 1;
				ip += index;
			}
		}
		return true;
	}

	public static boolean arePathsMatching(String httpPath, String controllerPath) {
		String[] controllerPathSegments = trimFront(controllerPath, "/").split("/+");
		String[] pathSegments = trimFront(httpPath, "/").split("/+");
		if (pathSegments.length < controllerPathSegments.length) {
			return false;
		}
		for (int i = 0; i < controllerPathSegments.length; ++i) {
			// We match exacts strings of all path segments unless controller path
			// segment is a path variable pattern. In that case we don't fail.
			if (!controllerPathSegments[i].equals(pathSegments[i]) && (!PATH_VARIABLE_PATTERN.matcher(controllerPathSegments[i]).find())) {
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

	// Method for extracting values of path variables.
	// E.g. request path /test/123/whatever/456 and method mapping /test/{id}/whatever/{id2}
	// should result in id='1'.
	public static String resolvePathVariableValue(String requestPath, String fullMethodMapping, PathVariable pathVariable) {
		String[] requestSegments = trimFront(requestPath, "/").split("/+");
		String[] controllerPathSegments = trimFront(fullMethodMapping, "/").split("/+");

		// We match request and controller path segment by segment and when the controller
		// segment is a path variable and when it equals the searched for path variable name
		// we return the string of the current request segment.
		int len = Math.min(requestSegments.length, controllerPathSegments.length);
		for (int i = 0; i < len; i++) {
			String contSeg = controllerPathSegments[i];
			Matcher matcher = PATH_VARIABLE_PATTERN.matcher(contSeg);
			if (matcher.find() && pathVariable.value().equals(matcher.group(2))) {
				return requestSegments[i];
			}
		}
		return null;
	}
}
