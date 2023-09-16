package com._7aske.grain.security.handler.proxy;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.HttpStatus;
import com._7aske.grain.requesthandler.handler.RequestHandler;
import com._7aske.grain.requesthandler.handler.proxy.AbstractRequestHandlerProxy;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.config.rule.RuleUrlPatternMatcher;

import java.io.IOException;

public class SecurityHandlerProxy extends AbstractRequestHandlerProxy {
	private final SecurityConfiguration securityConfiguration;

	public SecurityHandlerProxy(RequestHandler target, SecurityConfiguration securityConfiguration) {
		super(target);
		this.securityConfiguration = securityConfiguration;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response) {

		boolean result = new RuleUrlPatternMatcher(securityConfiguration.getRules()).matches(request);
		if (result) {
			try {
				target.handle(request, response);
			} catch (IOException e) {
				throw new GrainRuntimeException(e);
			}
		}
		else {
			throw new HttpException.Forbidden(HttpStatus.FORBIDDEN.getReason());
		}
	}
}