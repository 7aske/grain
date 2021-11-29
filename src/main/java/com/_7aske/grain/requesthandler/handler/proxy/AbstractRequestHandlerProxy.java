package com._7aske.grain.requesthandler.handler.proxy;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.requesthandler.handler.Handler;

public abstract class AbstractRequestHandlerProxy implements RequestHandlerProxy {
	protected Handler target;

	protected AbstractRequestHandlerProxy(Handler target) {
		this.target = target;
	}
	public abstract boolean handle(HttpRequest request, HttpResponse response, Session session);
}
