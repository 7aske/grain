package com._7aske.grain.http.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import static com._7aske.grain.util.ContentTypeUtil.probeContentTypeNoThrow;

public class AbstractView {
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final String path;
	private final String contentType;
	private String cachedContent = null;

	protected AbstractView(String path) {
		this.path = path;
		this.contentType = probeContentTypeNoThrow(Paths.get(path), "text/html");
	}

	public String getContent() {
		if (cachedContent == null) {
			try {
				InputStream inputStream = classLoader.getResourceAsStream(path);
				if (inputStream == null)
					cachedContent = "";
				else
					cachedContent = new String(inputStream.readAllBytes());
			} catch (IOException ex) {
				cachedContent = "";
			}
		}
		return cachedContent;
	}

	public String getContentType() {
		return this.contentType;
	}
}
