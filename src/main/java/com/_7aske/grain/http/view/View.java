package com._7aske.grain.http.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import static com._7aske.grain.util.ContentTypeUtil.probeContentTypeNoThrow;

public class View {
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final String path;
	private final String contentType;

	public View(String path) {
		this.path = path;
		this.contentType = probeContentTypeNoThrow(Paths.get(path), "text/html");
	}

	public String getContent() {
		try {
			InputStream inputStream = classLoader.getResourceAsStream(path);
			if (inputStream == null)
				return null;
			return new String(inputStream.readAllBytes());
		} catch (IOException ex) {
			return null;
		}
	}

	public String getContentType() {
		return this.contentType;
	}
}
