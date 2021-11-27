package com._7aske.grain.requesthandler.handler;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;

// @Refactor This should be an abstract class
public interface Handler {
	boolean handle(HttpRequest request, HttpResponse response, Session session);
}
