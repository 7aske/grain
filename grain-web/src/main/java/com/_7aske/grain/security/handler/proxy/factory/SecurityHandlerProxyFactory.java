package com._7aske.grain.security.handler.proxy.factory;

import com._7aske.grain.core.component.ConditionalOnExpression;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.handler.proxy.SecurityHandlerProxy;

@Grain
@Order(255)
@ConditionalOnExpression(ConfigurationKey.SECURITY_ENABLED)
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
