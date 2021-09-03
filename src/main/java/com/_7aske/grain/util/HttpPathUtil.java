package com._7aske.grain.util;


public class HttpPathUtil {
	private HttpPathUtil() {
	}

	public static boolean arePathsMatching(String httpPath, String controllerPath) {
		String[] controllerPathSegments = controllerPath.split("/+");
		String[] pathSegments = httpPath.split("/+");
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
}
