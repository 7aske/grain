package com._7aske.grain.requesthandler.middleware;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

public interface Middleware {
	void handle(HttpRequest request, HttpResponse response);
}
