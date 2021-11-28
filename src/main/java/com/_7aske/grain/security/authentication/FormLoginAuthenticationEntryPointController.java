package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Controller;
import com._7aske.grain.component.Inject;
import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.security.exception.GrainSecurityException;

// @Incomplete
@Controller
@RequestMapping("/login")
public class FormLoginAuthenticationEntryPointController {
	@Inject
	private AuthenticationEntryPoint entryPoint;
	@Inject
	private AuthenticationManager authenticationManager;

	// @Todo handle redirect after successful or unsuccessful login
	@RequestMapping(method = HttpMethod.POST)
	public String login(HttpRequest request) {
		try {

			Authentication authentication = entryPoint.authenticate(request);
			authentication = authenticationManager.authenticate(authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return authentication.getName();
		} catch (GrainSecurityException e) {
			throw new HttpException.Forbidden(e);
		}
	}
}
