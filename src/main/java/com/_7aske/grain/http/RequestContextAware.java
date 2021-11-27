package com._7aske.grain.http;

import com._7aske.grain.http.session.Session;

public interface RequestContextAware {
	HttpRequest getRequest();
	HttpResponse getResponse();
	Session getSession();
}
