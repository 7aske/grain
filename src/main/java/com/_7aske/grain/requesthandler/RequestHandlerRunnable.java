package com._7aske.grain.requesthandler;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpRequestParser;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;
import com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandlerRunnable implements Runnable {
	private final Socket socket;
	private final StaticHandlerRegistry staticHandlerRegistry;
	private final ControllerHandlerRegistry controllerRegistry;

	public RequestHandlerRunnable(GrainRegistry grainRegistry, StaticLocationsRegistry staticLocationsRegistry, Socket socket) {
		this.socket = socket;
		this.controllerRegistry = new ControllerHandlerRegistry(grainRegistry);
		this.staticHandlerRegistry = new StaticHandlerRegistry(staticLocationsRegistry);
	}

	@Override
	public void run() {
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {

			HttpRequestParser parser = new HttpRequestParser(reader);
			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();
			response.setStatus(HttpStatus.NOT_FOUND);

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
