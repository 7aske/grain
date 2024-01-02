package com._7aske.grain.web.requesthandler.handler.proxy;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

/**
 * HandlerProxy is a component in charge of proxying and potentially modifying or
 * blocking {@link RequestHandler#handle(HttpRequest, HttpResponse)} method calls.
 *
 * @see com._7aske.grain.security.handler.proxy.SecurityHandlerProxy
 * @see DefaultRequestHandlerProxy
 */
public abstract class AbstractRequestHandlerProxy implements RequestHandler {
	protected RequestHandler target;

	/**
	 * Constructs a new {@link AbstractRequestHandlerProxy} with the given {@link RequestHandler} as the target.
	 *
	 * @param target the {@link RequestHandler} to be proxied.
	 */
	protected AbstractRequestHandlerProxy(RequestHandler target) {
		this.target = target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(HttpRequest request) {
		// Usually, we want the proxy target to determine whether the proxy should
		// execute or not.
		return target.canHandle(request);
	}

	@Override
	public String getPath() {
		return target.getPath();
	}
}
