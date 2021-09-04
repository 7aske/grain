package com._7aske.grain.requesthandler.staticlocation;

import com._7aske.grain.http.HttpHeaders;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;
import com._7aske.grain.requesthandler.RequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry.RESOURCES_PREFIX;
import static com._7aske.grain.util.ContentTypeUtil.probeContentTypeNoThrow;
import static com._7aske.grain.util.HttpPathUtil.join;

public class StaticLocationHandler implements RequestHandler {
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final String location;
	private final boolean isResource;

	public StaticLocationHandler(String location) {
		this.isResource = location.startsWith(RESOURCES_PREFIX);
		if (isResource) {
			String loc = location.substring(RESOURCES_PREFIX.length());
			this.location = loc.startsWith("/") ? loc.substring(1) : loc;
		} else {
			this.location = location;
		}
	}


	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		Path path = Paths.get(getPath(), request.getPath());
		try (InputStream inputStream = getInputStream(path)) {
			response.setHeader(HttpHeaders.CONTENT_TYPE, probeContentTypeNoThrow(path, "text/html"));
			response.setBody(new String(inputStream.readAllBytes()));
			response.setStatus(HttpStatus.OK);
		} catch (IOException ex) {
			response.setStatus(HttpStatus.NOT_FOUND);
		}
	}

	private InputStream getInputStream(Path path) throws IOException{
		if (isResource) {
			URL url = classLoader.getResource(path.toString());
			if (url == null)
				throw new IOException();
			File file = new File(url.getPath());
			if (file.isDirectory()) {
				file = new File(url.getPath() + "/index.html");
			} else {
				return new FileInputStream(file);
			}
			if (!file.exists())
				throw new IOException();
			return new FileInputStream(file);
		} else {
			File file = new File(path.toString());
			if (!file.exists())
				throw new IOException();
			if (file.isDirectory()) {
				file = new File(path.resolve("index.html").toString());
			} else {
				return new FileInputStream(file);
			}
			if (!file.exists())
				throw new IOException();
			return new FileInputStream(file);
		}
	}

	@Override
	public boolean canHandle(String path) {
		if (isResource) {
			URL url = classLoader.getResource(join(getPath(), path));
			if (url == null) return false;
			File file = new File(url.getPath());
			if (file.isDirectory()) {
				file = new File(url.getPath() + "/index.html");
			}
			return file.exists();
		} else {
			return new File(Paths.get(getPath(), path).toAbsolutePath().toString()).exists();
		}
	}

	@Override
	public String getPath() {
		return location;
	}
}

