package com._7aske.grain.requesthandler.handler;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

public interface Handler {
	boolean handle(HttpRequest request, HttpResponse response);
}
