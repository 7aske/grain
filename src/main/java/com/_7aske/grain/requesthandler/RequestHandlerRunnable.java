package com._7aske.grain.requesthandler;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.*;
import com._7aske.grain.http.json.JsonDeserializer;
import com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry;
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
				JsonDeserializer deserializer = new JsonDeserializer((String) req.getBody());
				req.setBody(deserializer.parse());
			}
		});
	}

	@Override
	public void run() {
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {

			HttpRequestParser parser = new HttpRequestParser(reader);
			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();
			response.setStatus(HttpStatus.NOT_FOUND);

			middlewareRegistry.getHandlers(request.getPath())
					.forEach(handler -> handler.handle(request, response));
			controllerRegistry.getHandler(request.getPath())
					.ifPresent(handler -> handler.handle(request, response));
			staticHandlerRegistry.getHandler(request.getPath())
					.ifPresent(handler -> handler.handle(request, response));

			writer.write(response.getHttpString());
		} catch (IOException e) {
			throw new HttpException.InternalServerError(e);
		}
	}
}
