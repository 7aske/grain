package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Grain;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.UsernameAndPasswordAuthentication;

import java.util.Collections;

@Grain
public class FormLoginAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public Authentication authenticate(HttpRequest request) throws SecurityException {
		String username = request.getStringParameter("username");
		String password = request.getStringParameter("password");
		Authentication authentication = new UsernameAndPasswordAuthentication(username, password, Collections.emptyList());
		authentication.setAuthenticated(false);
		return authentication;
	}
}
