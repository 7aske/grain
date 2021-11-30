package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Controller;
import com._7aske.grain.component.Inject;
import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.*;
import com._7aske.grain.http.view.View;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.security.exception.GrainSecurityException;
import com._7aske.grain.ui.LoginPage;

/**
 * Default authentication entry point handling form POST requests to /login
 */
@Controller
@RequestMapping("/login")
public class FormLoginAuthenticationEntryPointController {
	@Inject
	private AuthenticationEntryPoint entryPoint;
	@Inject
	private LoginPage loginPage;

	// @Todo handle redirect after successful or unsuccessful login
	@RequestMapping(method = HttpMethod.POST)
	public String postLogin(HttpRequest request, HttpResponse response) {
		try {
			Authentication authentication = entryPoint.authenticate(request, response);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:/";
		} catch (GrainSecurityException e) {
			throw new HttpException.Forbidden(e);
		}
	}

	@RequestMapping(method = HttpMethod.GET)
	public View getLogin(HttpResponse response) {
		return loginPage;
	}
}
