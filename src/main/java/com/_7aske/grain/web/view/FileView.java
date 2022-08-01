package com._7aske.grain.web.view;

import com._7aske.grain.annotation.NotNull;

import java.io.IOException;
import java.io.InputStream;

import static com._7aske.grain.util.ContentTypeUtil.probeContentTypeNoThrow;

public class FileView implements View {
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final String path;
	private final String contentType;
	private String cachedContent = null;

	public FileView(String path) {
		this.path = path;
		// @Optimization cache content type for given path
		this.contentType = probeContentTypeNoThrow(path, "text/html");
	}

	public @NotNull String getContent() {
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


	@Override
	public @NotNull String getName() {
		return path;
	}

	public @NotNull String getContentType() {
		return this.contentType;
	}
}
