package com._7aske.grain.requesthandler.handler;

import com._7aske.grain.http.HttpMethod;

public interface RequestHandler extends Handler {
	String getPath();
	boolean canHandle(String path, HttpMethod method);
}
