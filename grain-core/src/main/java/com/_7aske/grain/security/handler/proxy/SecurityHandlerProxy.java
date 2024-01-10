package com._7aske.grain.security.handler.proxy;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.config.rule.RuleUrlPatternMatcher;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.proxy.AbstractRequestHandlerProxy;

public class SecurityHandlerProxy extends AbstractRequestHandlerProxy {
	private final SecurityConfiguration securityConfiguration;

	public SecurityHandlerProxy(RequestHandler target, SecurityConfiguration securityConfiguration) {
		super(target);
		this.securityConfiguration = securityConfiguration;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws Exception {

		boolean result = new RuleUrlPatternMatcher(securityConfiguration.getRules()).matches(request);
		if (result) {
			target.handle(request, response);
		} else {
			throw new HttpException.Forbidden("Access is denied");
		}
	}
}
