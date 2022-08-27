package com._7aske.grain.requesthandler.handler.proxy;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.handler.RequestHandler;

import java.io.IOException;

/**
 * This implementation of {@link AbstractRequestHandlerProxy} is basically a logging
 * no-op proxy.
 */
public final class DefaultRequestHandlerProxy extends AbstractRequestHandlerProxy {
	private final Logger logger = LoggerFactory.getLogger(DefaultRequestHandlerProxy.class);

	/**
	 * Constructs a new {@link DefaultRequestHandlerProxy} with the given {@link RequestHandler} as the target.
	 * @param target the {@link RequestHandler} to be proxied.
	 */
	public DefaultRequestHandlerProxy(RequestHandler target) {
		super(target);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws IOException {
		logger.trace("Proxying request for {}", target.getClass());
		target.handle(request, response);
	}
}
