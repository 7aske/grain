package com._7aske.grain.http.session;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;

public interface SessionInitializer {
	Session initialize(HttpRequest request, HttpResponse response);
}
