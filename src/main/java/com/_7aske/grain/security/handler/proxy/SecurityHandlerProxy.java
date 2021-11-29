package com._7aske.grain.security.handler.proxy;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.proxy.AbstractRequestHandlerProxy;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.config.rule.RuleUrlPatternMatcher;

public class SecurityHandlerProxy extends AbstractRequestHandlerProxy {
	private final SecurityConfiguration securityConfiguration;

	public SecurityHandlerProxy(Handler target, SecurityConfiguration securityConfiguration) {
		super(target);
		this.securityConfiguration = securityConfiguration;
	}

	@Override
	public boolean handle(HttpRequest request, HttpResponse response, Session session) {

		boolean result = new RuleUrlPatternMatcher(securityConfiguration.getRules()).matches(request);
		if (result)
			return target.handle(request, response, session);
		else
			throw new HttpException.Forbidden(HttpStatus.FORBIDDEN.getReason());
	}
}
