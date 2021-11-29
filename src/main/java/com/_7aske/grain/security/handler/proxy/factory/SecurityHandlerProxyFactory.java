package com._7aske.grain.security.handler.proxy.factory;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.proxy.RequestHandlerProxy;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.handler.proxy.SecurityHandlerProxy;

@Grain
public class SecurityHandlerProxyFactory implements HandlerProxyFactory {
	@Inject
	private SecurityConfiguration securityConfiguration;

	@Override
	public RequestHandlerProxy createProxy(Handler target) {
		return new SecurityHandlerProxy(target, securityConfiguration);
	}
}
