package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Controller;
import com._7aske.grain.component.Inject;
import com._7aske.grain.controller.annotation.RequestMapping;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.SessionStore;
import com._7aske.grain.http.session.SessionToken;
import com._7aske.grain.http.session.tokenprovider.HttpRequestTokenProvider;
import com._7aske.grain.http.view.View;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.security.exception.GrainSecurityException;
import com._7aske.grain.ui.LoginPage;

/**
 * Default authentication entry point handling form POST requests to /login
 */
@Controller
@RequestMapping
public class FormLoginAuthenticationEntryPointController {
	@Inject
	private AuthenticationEntryPoint entryPoint;
	@Inject
	private LoginPage loginPage;
	@Inject
	private SessionStore store;
	@Inject
	private HttpRequestTokenProvider provider;
	@Inject
	private SecurityConfiguration configuration;

	// @Todo handle redirect after successful or unsuccessful login
	@RequestMapping(value = "/login", method = HttpMethod.POST)
	public String postLogin(HttpRequest request, HttpResponse response) {
		try {
			Authentication authentication = entryPoint.authenticate(request, response);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:" + configuration.getAuthenticationSuccessUrl();
		} catch (GrainSecurityException e) {
			return "redirect:" + configuration.getAuthenticationFailureUrl();
		}
	}

	@RequestMapping(value = "/login", method = HttpMethod.GET)
	public View getLogin() {
		return loginPage;
	}

	@RequestMapping(value = "/logout", method = HttpMethod.GET)
	public String getLogout(HttpRequest request, HttpResponse response) {
		try {
			SessionToken token = provider.getSessionToken(request);
			if (token != null && store.hasSession(token.getId())) {
				store.invalidateSession(token.getId());
				SecurityContextHolder.getContext().setAuthentication(null);
			}
			return "redirect:/login";
		} catch (GrainSecurityException e) {
			throw new HttpException.Forbidden(e);
		}
	}
}
