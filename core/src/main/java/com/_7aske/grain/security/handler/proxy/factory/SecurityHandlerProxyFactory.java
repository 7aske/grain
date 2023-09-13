package com._7aske.grain.security.handler.proxy.factory;

import com._7aske.grain.core.component.Condition;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.handler.proxy.SecurityHandlerProxy;

@Grain
@Order(255)
@Condition("security.enabled")
public class SecurityHandlerProxyFactory implements HandlerProxyFactory {
	private final SecurityConfiguration securityConfiguration;

	public SecurityHandlerProxyFactory(SecurityConfiguration securityConfiguration) {
		this.securityConfiguration = securityConfiguration;
	}

	@Override
	public RequestHandler createProxy(RequestHandler target) {
		return new SecurityHandlerProxy(target, securityConfiguration);
	}
}
