package com._7aske.grain.requesthandler.staticlocation;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.*;
import com._7aske.grain.requesthandler.handler.RequestHandler;

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
	private static final String INDEX_PAGE = "index.html";

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
		Path path = Paths.get(location, request.getPath());
		try (InputStream inputStream = getInputStream(path)) {
			response.setHeader(HttpHeaders.CONTENT_TYPE, probeContentTypeNoThrow(path, "text/html"));
			response.getOutputStream().write(inputStream.readAllBytes());
			response.setStatus(HttpStatus.OK);
			// Finally, we need to set the request handled attribute to true
			// so that we don't get 404 exception from the HandlerRunner.
			request.setHandled(true);
		} catch (IOException ex) {
			throw new HttpException.NotFound(request.getPath());
		}
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		HttpMethod method = request.getMethod();
		String path = request.getPath();

		// we allow only GET http methods
		if (!method.equals(HttpMethod.GET)) return false;

		if (isResource) {
			String joined = join(location, path);
			URL url = classLoader.getResource(joined);
			if (url == null) {
				joined = join(joined, INDEX_PAGE);
				url = classLoader.getResource(joined);
			}
			return url != null;
		} else {
			return new File(Paths.get(location, path).toAbsolutePath().toString()).exists();
		}
	}

	private InputStream getInputStream(Path path) throws IOException {
		if (isResource) {
			InputStream resourceAsStream = classLoader.getResourceAsStream(path.toString());
			if (resourceAsStream == null) {
				resourceAsStream = classLoader.getResourceAsStream(join(path.toString(), INDEX_PAGE));
			}
			return resourceAsStream;
		} else {
			File file = new File(path.toString());
			if (!file.exists())
				throw new IOException();
			if (file.isDirectory()) {
				file = new File(path.resolve(INDEX_PAGE).toString());
			} else {
				return new FileInputStream(file);
			}
			if (!file.exists())
				throw new IOException();
			return new FileInputStream(file);
		}
	}

	@Override
	public String getPath() {
		return location;
	}
}

