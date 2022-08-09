package com._7aske.grain.web.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.security.Authentication;

import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

@Grain
@Order(256)
public class GtlViewResolver implements ViewResolver {
	private final Configuration configuration;

	public GtlViewResolver(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
		// Setting implicit objects
		view.addAttribute("#request", request);
		view.addAttribute("#session", session);
		view.addAttribute("#authentication", authentication);
		view.addAttribute("#configuration", configuration);

		response.setHeader(CONTENT_TYPE, view.getContentType());
		response.setBody(Interpreter.interpret(view.getContent(), view.getAttributes()));
	}
}
