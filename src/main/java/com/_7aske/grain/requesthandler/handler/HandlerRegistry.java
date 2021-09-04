package com._7aske.grain.requesthandler.handler;

import com._7aske.grain.http.HttpMethod;

import java.util.List;

public interface HandlerRegistry {
	boolean canHandle(String path, HttpMethod method);
	List<Handler> getHandlers(String path, HttpMethod method);
}
