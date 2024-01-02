package com._7aske.grain.web.requesthandler.handler.proxy.factory;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.DefaultRequestHandlerProxy;

@Grain
@Order(256)
public class DefaultRequestHandlerProxyFactory implements HandlerProxyFactory {
	@Override
	public DefaultRequestHandlerProxy createProxy(RequestHandler target) {
		return new DefaultRequestHandlerProxy(target);
	}
}
