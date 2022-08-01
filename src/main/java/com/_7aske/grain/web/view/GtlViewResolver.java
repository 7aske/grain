package com._7aske.grain.web.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.component.Default;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.security.Authentication;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

@Grain
@Default
public class GtlViewResolver implements ViewResolver {

	@Override
	public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
		// Setting implicit objects
		view.addAttribute("request", request);
		view.addAttribute("session", session);
		view.addAttribute("authentication", authentication);
		response.setHeader(CONTENT_TYPE, view.getContentType());
		response.setBody(Interpreter.interpret(view.getContent(), view.getAttributes()));
	}
}
