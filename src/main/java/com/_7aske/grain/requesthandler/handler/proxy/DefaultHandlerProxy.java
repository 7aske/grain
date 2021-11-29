package com._7aske.grain.requesthandler.handler.proxy;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.handler.Handler;

public final class DefaultHandlerProxy extends AbstractRequestHandlerProxy {
	private final Logger logger = LoggerFactory.getLogger(DefaultHandlerProxy.class);
	public DefaultHandlerProxy(Handler target) {
		super(target);
	}

	@Override
	public boolean handle(HttpRequest request, HttpResponse response, Session session) {
		logger.trace("Proxying request for {}", target.getClass());
		return target.handle(request, response, session);
	}
}
