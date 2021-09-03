package com._7aske.grain.util;


public class HttpPathUtil {
	private HttpPathUtil() {
	}

	public static boolean arePathsMatching(String httpPath, String controllerPath) {
		String _httpPath = httpPath.startsWith("/") ? httpPath.substring(1) : httpPath;
		String _controllerPath = controllerPath.startsWith("/") ? controllerPath.substring(1) : controllerPath;
		String[] controllerPathSegments = _controllerPath.split("/+");
		String[] pathSegments = _httpPath.split("/+");
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
