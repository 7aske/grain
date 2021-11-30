package com._7aske.grain.requesthandler;

import com._7aske.grain.config.Configuration;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.ui.impl.ErrorPage;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.*;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.http.session.SessionInitializer;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.controller.ControllerHandlerRegistry;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.requesthandler.handler.runner.HandlerRunner;
import com._7aske.grain.requesthandler.handler.runner.HandlerRunnerFactory;
import com._7aske.grain.requesthandler.middleware.MiddlewareHandlerRegistry;
import com._7aske.grain.requesthandler.staticlocation.StaticHandlerRegistry;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.authentication.provider.HttpRequestAuthenticationProviderStrategy;
import com._7aske.grain.security.context.SecurityContextHolder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import static com._7aske.grain.config.Configuration.Key.REQUEST_HANDLER_ACCESS_LOG;
import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

public class RequestHandlerRunnable implements Runnable {
	private final Socket socket;
	private final Configuration configuration;
	private final HandlerRunner handlerRunner;
	private final Logger logger = LoggerFactory.getLogger(RequestHandlerRunnable.class);
	private final ApplicationContext context;
	private final SessionInitializer sessionInitializer;
	private HttpRequest httpRequest;
	private HttpResponse httpResponse;
	private Session session;

	public RequestHandlerRunnable(ApplicationContext context, Socket socket) {
		this.socket = socket;
		// @Todo Make static handler registry a Grain
		StaticHandlerRegistry staticHandlerRegistry = new StaticHandlerRegistry(context.getStaticLocationsRegistry());
		ControllerHandlerRegistry controllerRegistry = context.getGrain(ControllerHandlerRegistry.class);
		MiddlewareHandlerRegistry middlewareRegistry = context.getGrain(MiddlewareHandlerRegistry.class);
		this.sessionInitializer = context.getGrain(SessionInitializer.class);
		this.configuration = context.getConfiguration();
		this.context = context;
		HandlerProxyFactory proxyFactory = context.getGrain(HandlerProxyFactory.class);


		this.handlerRunner = HandlerRunnerFactory.getRunner(proxyFactory)
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
			Session session = sessionInitializer.initialize(request, response);

			// @Refactor to authentication manager or something
			HttpRequestAuthenticationProviderStrategy provider = context.getGrain(HttpRequestAuthenticationProviderStrategy.class);
			Authentication authentication = provider.getAuthentication(request);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			this.httpRequest = request;
			this.httpResponse = response;
			this.session = session;

			try {

				handlerRunner.handle(request, response, session);

			} catch (HttpException ex) {
				response.setStatus(ex.getStatus());
				if (response.getBody() == null) {
					response.setHeader(CONTENT_TYPE, HttpContentType.TEXT_HTML)
							.setBody(ex.getHtmlMessage());
				}
				// @Refactor we create coupling here
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
						.setHeader(CONTENT_TYPE, HttpContentType.TEXT_HTML)
						.setBody(ErrorPage.getDefault(ex, request.getPath()));
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
