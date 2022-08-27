package com._7aske.grain.web.view;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.security.Authentication;

public interface ViewResolver {
	void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication);

	default boolean supports(View view) {
		return true;
	}

	default void populateImplicitObjects(View view,
	                                     HttpRequest request,
	                                     HttpResponse response,
	                                     Session session,
	                                     Authentication authentication,
	                                     Configuration configuration) {
		view.addAttribute("#request", request);
		view.addAttribute("#response", response);
		view.addAttribute("#session", session);
		view.addAttribute("#authentication", authentication);
		view.addAttribute("#configuration", configuration);
	}
}
