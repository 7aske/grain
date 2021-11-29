package com._7aske.grain.requesthandler.handler.proxy.factory;

import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.proxy.RequestHandlerProxy;

public interface HandlerProxyFactory {
	RequestHandlerProxy createProxy(Handler target);
}
