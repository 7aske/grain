package com._7aske.grain.requesthandler;

import java.util.Optional;

public interface HandlerRegistry {
	boolean canHandle(String Path);
	Optional<RequestHandler> getHandler(String path);
}
