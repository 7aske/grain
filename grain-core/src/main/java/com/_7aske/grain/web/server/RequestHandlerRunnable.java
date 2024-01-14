package com._7aske.grain.web.server;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.authentication.provider.HttpRequestAuthenticationProviderStrategy;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.util.HttpPathUtil;
import com._7aske.grain.web.controller.exceptionhandler.ExceptionControllerHandler;
import com._7aske.grain.web.http.GrainHttpRequest;
import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpHeaders;
import com._7aske.grain.web.http.codec.json.JsonWriter;
import com._7aske.grain.web.http.session.Session;
import com._7aske.grain.web.http.session.SessionInitializer;
import com._7aske.grain.web.requesthandler.handler.runner.HandlerRunner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static com._7aske.grain.core.configuration.ConfigurationKey.REQUEST_HANDLER_ACCESS_LOG;
import static com._7aske.grain.core.configuration.ConfigurationKey.SERVER_CONTEXT_PATH;

public class RequestHandlerRunnable implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(RequestHandlerRunnable.class);
	private final Socket socket;
	private final Configuration configuration;
	private final HandlerRunner handlerRunner;
	private final SessionInitializer sessionInitializer;
	private final HttpRequestAuthenticationProviderStrategy provider;
	private final JsonWriter jsonWriter = new JsonWriter(false);
	private final boolean logEnabled;
	private final boolean sessionEnabled;
	private final SimpleDateFormat DATE_HEADER_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	private final String SERVER_NAME = "Silo";
	private final String contextPath;
	private final ExceptionControllerHandler errorHandler;

	public RequestHandlerRunnable(ApplicationContext context, Socket socket) {
		this.socket = socket;
		this.configuration = context.getConfiguration();
		this.handlerRunner = context.getGrain(HandlerRunner.class);
		this.sessionInitializer = context.getGrain(SessionInitializer.class);
		this.provider = context.getGrain(HttpRequestAuthenticationProviderStrategy.class);
		this.logEnabled = configuration.getBoolean(REQUEST_HANDLER_ACCESS_LOG, true);
		this.sessionEnabled = configuration.getBoolean(ConfigurationKey.SESSION_ENABLED, true);
		this.errorHandler = context.getGrain(ExceptionControllerHandler.class);
		this.contextPath = this.configuration.get(SERVER_CONTEXT_PATH, "/") + "/**";
	}

	@Override
	public void run() {
		Instant start = Instant.now();
		try (HttpRequestReader reader = new HttpRequestReader(new BufferedInputStream(socket.getInputStream()));
		     HttpResponseWriter writer = new HttpResponseWriter(socket.getOutputStream())) {

			GrainHttpRequest request = reader.readHttpRequest();

			setupAddresses(request);

			GrainHttpResponse response = new GrainHttpResponse();

			response.setHeader(HttpHeaders.SERVER, SERVER_NAME);
			response.setHeader(HttpHeaders.DATE, DATE_HEADER_FORMAT.format(start.toEpochMilli()));

			if (sessionEnabled) {
				Session session = sessionInitializer.initialize(request, response);
				request.setSession(session);
			}

			Authentication authentication = provider.getAuthentication(request);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			try {
				if (!HttpPathUtil.antMatching(contextPath, request.getPath())){
					throw new HttpException.NotFound(request.getPath());
				}

				handlerRunner.handle(request, response);
			} catch (Exception ex) {
				Throwable actual = ex.getCause() != null ? ex.getCause() : ex;
				errorHandler.handle(actual, request, response);
			} finally {
				writer.write(response);
				Instant end = Instant.now();
				if (logEnabled) {
					logger.info("{} {} - {} - {}ms",
							request.getMethod(),
							request.getPath(),
							response.getStatus(),
							end.toEpochMilli() - start.toEpochMilli());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error while handling request", e);
		}
	}

	private void setupAddresses(GrainHttpRequest request) {
		request.setRemoteAddr(socket.getInetAddress().getHostAddress());
		request.setRemoteHost(socket.getInetAddress().getCanonicalHostName());
		request.setRemotePort(socket.getPort());
		request.setLocalAddr(socket.getLocalAddress().getHostAddress());
		request.setLocalName(socket.getLocalAddress().getCanonicalHostName());
		request.setLocalPort(socket.getLocalPort());
	}
}
