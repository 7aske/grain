package com._7aske.grain.web.requesthandler.staticlocation;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.web.exception.HttpException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.web.http.*;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static com._7aske.grain.logging.LoggerFactory.*;
import static com._7aske.grain.util.ContentTypeUtil.probeContentTypeNoThrow;
import static com._7aske.grain.web.util.HttpPathUtil.join;

public class StaticLocationHandler implements RequestHandler {
	private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final String location;
	private final boolean isResource;
	private static final String INDEX_PAGE = "index.html";
	private final Logger logger;

	public StaticLocationHandler(String location) {
		this.isResource = location.startsWith(StaticLocationsRegistry.RESOURCES_PREFIX);
		if (isResource) {
			String loc = location.substring(StaticLocationsRegistry.RESOURCES_PREFIX.length());
			this.location = loc.startsWith("/") ? loc.substring(1) : loc;
		} else {
			this.location = location;
		}
		this.logger = getLogger(getClass());
	}


	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		logger.debug("Handling {} {}", request.getMethod(), request.getPath());
		Path path = Paths.get(location, request.getPath());
		try (InputStream inputStream = getInputStream(path)) {
			response.setHeader(HttpHeaders.CONTENT_TYPE, probeContentTypeNoThrow(path, "text/html"));
			response.getOutputStream().write(inputStream.readAllBytes());
			response.setStatus(HttpStatus.OK);
			// Finally, we need to set the request handled attribute to true
			// so that we don't get 404 exception from the HandlerRunner.
			if (response instanceof GrainHttpResponse res) {
				res.setCommitted(true);
			}
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

			if (url == null) {
				return false;
			}

			return Paths.get(url.getPath()).toFile().isFile();
		} else {
			return Paths.get(location, path).toAbsolutePath().toFile().exists();
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
	public @NotNull String getPath() {
		return location;
	}

	@Override
	public Collection<HttpMethod> getMethods() {
		return List.of(HttpMethod.GET);
	}

}

