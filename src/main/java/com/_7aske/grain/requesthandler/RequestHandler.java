package com._7aske.grain.requesthandler;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

public interface RequestHandler {
	void handle(HttpRequest request, HttpResponse response);
}
