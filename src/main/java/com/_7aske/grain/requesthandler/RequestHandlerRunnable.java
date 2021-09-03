package com._7aske.grain.requesthandler;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpRequestParser;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.requesthandler.controller.ControllerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandlerRunnable implements Runnable {
	private final Socket socket;
	// private final StaticHandler staticHandler;
	private final ControllerRegistry controllerRegistry;

	public RequestHandlerRunnable(GrainRegistry grainRegistry, StaticLocationsRegistry staticLocationsRegistry, Socket socket) {
		this.socket = socket;
		this.controllerRegistry = new ControllerRegistry(grainRegistry);
		// this.staticHandler = new StaticHandler(staticLocationsRegistry);
	}

	@Override
	public void run() {
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {

			HttpRequestParser parser = new HttpRequestParser(reader);
			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();

			controllerRegistry.getControllerForPath(request.getPath())
					.ifPresent(controller -> controller.handle(request, response));

			writer.write(response.getHttpString());
		} catch (IOException e) {
			throw new HttpException.InternalServerError(e);
		}
	}
}
