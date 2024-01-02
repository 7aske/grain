package com._7aske.grain.web.view;

import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.session.Session;
import com._7aske.grain.security.Authentication;

import java.io.IOException;
import java.io.OutputStream;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
@Order(256)
public class GtlViewResolver implements ViewResolver {
	private final Configuration configuration;

	public GtlViewResolver(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public boolean supports(View view) {
		return view.getName().toLowerCase().endsWith(".gtl");
	}

	@Override
	public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
		populateImplicitObjects(view, request, response, session, authentication, configuration);

		response.setHeader(CONTENT_TYPE, view.getContentType());
		try (OutputStream outputStream = response.getOutputStream()) {
			byte[] output = Interpreter.interpret(view.getContent(), view.getAttributes()).getBytes();
			outputStream.write(output);
			response.setContentType(view.getContentType());
			response.setContentLength(output.length);
			outputStream.flush();
		} catch (IOException e) {
			throw new GrainRuntimeException(e);
		}
	}
}
