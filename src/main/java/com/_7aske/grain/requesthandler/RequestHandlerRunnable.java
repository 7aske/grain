package com._7aske.grain.requesthandler;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.exception.ErrorPageBuilder;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpRequestParser;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;
import com._7aske.grain.http.json.JsonParser;
import com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.requesthandler.handler.runner.HandlerRunnerFactory;
import com._7aske.grain.requesthandler.middleware.MiddlewareHandlerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class RequestHandlerRunnable implements Runnable {
	private final Socket socket;
	private final StaticHandlerRegistry staticHandlerRegistry;
	private final ControllerHandlerRegistry controllerRegistry;
	private final MiddlewareHandlerRegistry middlewareRegistry;

	public RequestHandlerRunnable(GrainRegistry grainRegistry, StaticLocationsRegistry staticLocationsRegistry, Socket socket) {
		this.socket = socket;
		this.controllerRegistry = new ControllerHandlerRegistry(grainRegistry);
		this.staticHandlerRegistry = new StaticHandlerRegistry(staticLocationsRegistry);
		this.middlewareRegistry = new MiddlewareHandlerRegistry(grainRegistry);

		middlewareRegistry.addHandler((req, res) -> {
			if (req.hasHeader(CONTENT_TYPE) && req.getHeader(CONTENT_TYPE).equals("application/json")) {
				JsonParser deserializer = new JsonParser((String) req.getBody());
				req.setBody(deserializer.parse());
			}
			return false;
		});
	}

	@Override
	public void run() {
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			HttpRequestParser parser = new HttpRequestParser(reader);
			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();

			try {
				HandlerRunnerFactory.getRunner()
						.addRegistry(middlewareRegistry)
						.addRegistry(controllerRegistry)
						.addRegistry(staticHandlerRegistry)
						.run(request, response);

			} catch (HttpException ex) {
				ex.printStackTrace();
				response.setStatus(ex.getStatus());
				if (response.getBody() == null) {
					response.setHeader(CONTENT_TYPE, "text/html")
							.setBody(ex.getHtmlMessage());
				}
			} catch (GrainRuntimeException ex) {
				ex.printStackTrace();
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
						.setHeader(CONTENT_TYPE, "text/html")
						.setBody(ErrorPageBuilder.getDefaultErrorPage(ex, request.getPath()));
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
						.setHeader(CONTENT_TYPE, "text/html")
						.setBody(ErrorPageBuilder.getDefaultErrorPage(ex, request.getPath()));
			} finally {
				writer.write(response.getHttpString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
