package com._7aske.grain.requesthandler;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.*;
import com._7aske.grain.http.json.JsonWriter;
import com._7aske.grain.http.json.nodes.JsonObjectNode;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.http.session.SessionInitializer;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.handler.runner.HandlerRunner;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.authentication.provider.HttpRequestAuthenticationProviderStrategy;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.ui.impl.ErrorPage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import static com._7aske.grain.core.configuration.ConfigurationKey.REQUEST_HANDLER_ACCESS_LOG;
import static com._7aske.grain.http.HttpHeaders.ACCEPT;
import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class RequestHandlerRunnable implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(RequestHandlerRunnable.class);
	private final Socket socket;
	private final Configuration configuration;
	private final HandlerRunner handlerRunner;
	private final SessionInitializer sessionInitializer;
	private final HttpRequestAuthenticationProviderStrategy provider;
	private final JsonWriter jsonWriter = new JsonWriter(false);

	public RequestHandlerRunnable(ApplicationContext context, Socket socket) {
		this.socket = socket;
		this.configuration = context.getConfiguration();
		this.handlerRunner = context.getGrain(HandlerRunner.class);
		this.sessionInitializer = context.getGrain(SessionInitializer.class);
		this.provider = context.getGrain(HttpRequestAuthenticationProviderStrategy.class);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try (BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
		     OutputStream socketOut = socket.getOutputStream()) {

			HttpRequestParser parser = new HttpRequestParser(reader);
			HttpRequest request = parser.getHttpRequest();
			HttpResponse response = new HttpResponse();
			HttpResponseWriter writer = new HttpResponseWriter(response);

			Session session = sessionInitializer.initialize(request, response);
			request.setSession(session);

			Authentication authentication = provider.getAuthentication(request);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			try {
				handlerRunner.handle(request, response);
			} catch (HttpException ex) {
				writeHttpExceptionResponse(request, response, ex);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				writeRuntimeExceptionResponse(request, response, ex);
			} finally {
				writer.writeTo(socketOut);
				long end = System.currentTimeMillis();
				if (configuration.getBoolean(REQUEST_HANDLER_ACCESS_LOG, true)) {
					logger.info("{} {} {} - {} - {}ms",
							request.getMethod(),
							request.getPath(),
							request.getVersion(),
							response.getStatus().getValue(),
							end - start);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeRuntimeExceptionResponse(HttpRequest request, HttpResponse response, RuntimeException ex) throws IOException {
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		if (Objects.equals(request.getHeader(ACCEPT), HttpContentType.APPLICATION_JSON) ||
				Objects.equals(request.getHeader(CONTENT_TYPE), HttpContentType.APPLICATION_JSON)) {
			JsonObjectNode jsonObject = new JsonObjectNode();
			jsonObject.putString("error", ex.getMessage());
			jsonObject.putString("status", HttpStatus.INTERNAL_SERVER_ERROR.getReason());
			jsonObject.putNumber("code", HttpStatus.INTERNAL_SERVER_ERROR.getValue());
			jsonObject.putString("path", request.getPath());
			response.setHeader(CONTENT_TYPE, HttpContentType.APPLICATION_JSON);
			try(OutputStream outputStream = response.getOutputStream()) {
				jsonWriter.write(jsonObject, outputStream);
			}
		} else {
			response.setHeader(CONTENT_TYPE, HttpContentType.TEXT_HTML);
			response.getOutputStream().write(ErrorPage.getDefault(ex, request.getPath()).getBytes());
		}
	}

	private void writeHttpExceptionResponse(HttpRequest request, HttpResponse response, HttpException ex) throws IOException {
		response.setStatus(ex.getStatus());
		if (Objects.equals(request.getHeader(ACCEPT), HttpContentType.APPLICATION_JSON) ||
				Objects.equals(request.getHeader(CONTENT_TYPE), HttpContentType.APPLICATION_JSON)) {
			if (response.length() == 0) {
				response.setHeader(CONTENT_TYPE, HttpContentType.APPLICATION_JSON);
				JsonObjectNode jsonObject = new JsonObjectNode();
				jsonObject.putString("error", ex.getMessage());
				jsonObject.putString("status", ex.getStatus().getReason());
				jsonObject.putNumber("code", ex.getStatus().getValue());
				jsonObject.putString("path", ex.getPath());
				try(OutputStream outputStream = response.getOutputStream()) {
					jsonWriter.write(jsonObject, outputStream);
				}
			}
		} else {
			if (response.length() == 0) {
				response.setHeader(CONTENT_TYPE, HttpContentType.TEXT_HTML);
				response.getOutputStream().write(ex.getHtmlMessage().getBytes());
			}
		}
	}
}
