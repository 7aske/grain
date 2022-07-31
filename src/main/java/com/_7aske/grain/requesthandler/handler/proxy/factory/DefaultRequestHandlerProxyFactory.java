package com._7aske.grain.requesthandler.handler.proxy.factory;

import com._7aske.grain.core.component.Default;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.proxy.DefaultHandlerProxy;
import com._7aske.grain.requesthandler.handler.proxy.RequestHandlerProxy;

@Grain
@Default
public class DefaultRequestHandlerProxyFactory implements HandlerProxyFactory {
	@Override
	public RequestHandlerProxy createProxy(Handler target) {
		return new DefaultHandlerProxy(target);
	}
}
