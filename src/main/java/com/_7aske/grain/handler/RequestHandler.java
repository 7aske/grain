package com._7aske.grain.handler;

import com._7aske.grain.http.HttpRequestParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			HttpRequestParser parser = new HttpRequestParser(reader);
			writer.write("HTTP/1.1 200 OK\r\n\r\n");
			writer.write(parser.getHttpRequest().toString());
			writer.flush();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
