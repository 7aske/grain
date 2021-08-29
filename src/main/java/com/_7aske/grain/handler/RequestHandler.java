package com._7aske.grain.handler;

import java.net.Socket;

public class RequestHandler implements Runnable {
	private final Socket socket;

	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	public static RequestHandler handle(Socket socket) {
		return new RequestHandler(socket);
	}

	@Override
	public void run() {
	}
}
