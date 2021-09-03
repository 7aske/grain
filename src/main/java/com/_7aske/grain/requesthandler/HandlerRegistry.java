package com._7aske.grain.requesthandler;

import com._7aske.grain.http.HttpRequest;

public interface HandlerRegistry {
	boolean canHandle(HttpRequest request);
}
