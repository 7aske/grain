package com._7aske.grain.http.view;

import javax.swing.text.Caret;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class View {
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final String path;
	private String contentType;

	public View(String path) {
		this.path = path;
		try {
			this.contentType = Files.probeContentType(Paths.get(path));
		} catch (IOException e) {
			this.contentType = "text/plain";
			e.printStackTrace();
		}
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
