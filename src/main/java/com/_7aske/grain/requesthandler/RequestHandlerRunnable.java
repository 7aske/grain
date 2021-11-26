package com._7aske.grain.requesthandler;

import com._7aske.grain.config.Configuration;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.exception.ErrorPageBuilder;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpRequestParser;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;
import com._7aske.grain.http.json.JsonParser;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.requesthandler.handler.runner.HandlerRunner;
import com._7aske.grain.requesthandler.handler.runner.HandlerRunnerFactory;
import com._7aske.grain.requesthandler.middleware.MiddlewareHandlerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import static com._7aske.grain.config.Configuration.Key.REQUEST_HANDLER_ACCESS_LOG;
import static com._7aske.grain.http.HttpContentType.APPLICATION_JSON;
import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class RequestHandlerRunnable implements Runnable {
	private final Socket socket;
	private final Configuration configuration;
	private final HandlerRunner<?> handlerRunner;
	private final Logger logger = LoggerFactory.getLogger(RequestHandlerRunnable.class);

	public RequestHandlerRunnable(ApplicationContext context, Socket socket) {
		this.socket = socket;
		ControllerHandlerRegistry controllerRegistry = new ControllerHandlerRegistry(context.getGrainRegistry());
		StaticHandlerRegistry staticHandlerRegistry = new StaticHandlerRegistry(context.getStaticLocationsRegistry());
		MiddlewareHandlerRegistry middlewareRegistry = new MiddlewareHandlerRegistry(context.getGrainRegistry());
		this.configuration = context.getConfiguration();

		// Used for parsing JSON body
		middlewareRegistry.addHandler((req, res) -> {
			if (Objects.equals(req.getHeader(CONTENT_TYPE), APPLICATION_JSON)) {
				JsonParser deserializer = new JsonParser((String) req.getBody());
				req.setBody(deserializer.parse());
			}
			return false;
		});

		this.handlerRunner = HandlerRunnerFactory.getRunner()
				.addRegistry(middlewareRegistry)
				.addRegistry(controllerRegistry)
				.addRegistry(staticHandlerRegistry);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			HttpRequestParser parser = new HttpRequestParser(reader);
			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();

			try {

				handlerRunner.run(request, response);

			} catch (HttpException ex) {
				response.setStatus(ex.getStatus());
				if (response.getBody() == null) {
					response.setHeader(CONTENT_TYPE, "text/html")
							.setBody(ex.getHtmlMessage());
				}
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
						.setHeader(CONTENT_TYPE, "text/html")
						.setBody(ErrorPageBuilder.getDefaultErrorPage(ex, request.getPath()));
			} finally {
				long end = System.currentTimeMillis();
				writer.write(response.getHttpString());
				if (Objects.equals(configuration.getProperty(REQUEST_HANDLER_ACCESS_LOG), true)) {
					logger.info("{} {} {} - {} - {}ms", request.getMethod(), request.getPath(), request.getVersion(), response.getStatus().getValue(), end - start);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
