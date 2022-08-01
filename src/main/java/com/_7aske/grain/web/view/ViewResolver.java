package com._7aske.grain.web.view;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.security.Authentication;

public interface ViewResolver {
	void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication);
}
