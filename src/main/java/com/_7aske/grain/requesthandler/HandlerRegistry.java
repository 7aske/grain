package com._7aske.grain.requesthandler;

import com._7aske.grain.http.HttpMethod;

import java.util.Optional;

public interface HandlerRegistry {
	boolean canHandle(String path, HttpMethod method);
	Optional<RequestHandler> getHandler(String path, HttpMethod method);
}
