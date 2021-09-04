package com._7aske.grain.requesthandler;

import com._7aske.grain.http.HttpMethod;

public interface RequestHandler extends Handler {
	String getPath();
	boolean canHandle(String path, HttpMethod method);
}
