package com._7aske.grain.security.authentication;

import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.controller.annotation.Controller;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.web.exception.HttpException;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.config.SecurityConfiguration;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.security.exception.GrainSecurityException;
import com._7aske.grain.web.ui.LoginPage;
import com._7aske.grain.web.controller.annotation.RequestMapping;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.session.SessionStore;
import com._7aske.grain.web.http.session.SessionToken;
import com._7aske.grain.web.http.session.tokenprovider.HttpRequestTokenProvider;
import com._7aske.grain.web.view.View;

/**
 * Default authentication entry point handling form POST requests to /login
 */
@Controller
@Order(Order.LOWEST_PRECEDENCE)
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
			return "redirect:" + configuration.getAuthenticationFailureUrl() + "?error";
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
			return "redirect:/login?logout";
		} catch (GrainSecurityException e) {
			throw new HttpException.Forbidden(e);
		}
	}
}
