package com._7aske.grain.web.requesthandler.handler.proxy.factory;

import com._7aske.grain.web.requesthandler.handler.RequestHandler;

/**
 * Factory interface for creating {@link RequestHandler} proxies.
 */
public interface HandlerProxyFactory {
	/**
	 * Creates a new {@link RequestHandler} proxy.
	 *
	 * @param target the {@link RequestHandler} to be proxied.
	 * @return the new {@link RequestHandler} proxy.
	 */
	RequestHandler createProxy(RequestHandler target);
}
