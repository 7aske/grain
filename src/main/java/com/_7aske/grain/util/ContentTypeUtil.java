package com._7aske.grain.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentTypeUtil {
	private ContentTypeUtil(){}

	public static String probeContentTypeNoThrow(String path) {
		try {
			return Files.probeContentType(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String probeContentTypeNoThrow(Path path) {
		try {
			return Files.probeContentType(path);
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String probeContentTypeNoThrow(Path path, String fallback) {
		String contentType = probeContentTypeNoThrow(path);
		return contentType == null ? fallback : contentType;
	}

	public static String probeContentTypeNoThrow(String path, String fallback) {
		String contentType = probeContentTypeNoThrow(path);
		return contentType == null ? fallback : contentType;
	}
}
