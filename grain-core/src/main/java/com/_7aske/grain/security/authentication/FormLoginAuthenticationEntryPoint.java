package com._7aske.grain.security.authentication;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.session.Cookie;
import com._7aske.grain.web.http.session.SessionConstants;
import com._7aske.grain.web.http.session.SessionStore;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.CookieAuthentication;
import com._7aske.grain.security.SecurityConstants;
import com._7aske.grain.security.User;
import com._7aske.grain.security.crypto.PasswordEncoder;
import com._7aske.grain.security.exception.*;
import com._7aske.grain.security.service.UserService;

import java.util.UUID;

import static com._7aske.grain.web.http.session.SessionConstants.SESSION_COOKIE_NAME;

@Grain
public class FormLoginAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Inject
	private UserService userService;
	@Inject
	private PasswordEncoder passwordEncoder;
	@Inject
	private SessionStore sessionStore;

	@Override
	public Authentication authenticate(HttpRequest request, HttpResponse response) throws GrainSecurityException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		User user = userService.findByUsername(username);

		if (user == null)
			throw new UserNotFoundException("User not found");

		if (!user.isEnabled())
			throw new UserDisabledException("User is disabled");

		if (password == null || !passwordEncoder.matches(password, (String) user.getPassword()))
			throw new InvalidCredentialsException("Invalid credentials");

		// @Refactor can possibly return empty token instead of throwing
		if (user.isCredentialsExpired())
			throw new CredentialsExpiredException("Credentials expired");

		Cookie gsid = new Cookie(SESSION_COOKIE_NAME, UUID.randomUUID().toString());
		gsid.setMaxAge(System.currentTimeMillis() / 1000 + SessionConstants.SESSION_DEFAULT_MAX_AGE);
		Authentication authentication = new CookieAuthentication(username, gsid, user.getAuthorities());
		sessionStore.setToken(gsid.getId(), gsid);
		sessionStore.put(gsid.getId(), SecurityConstants.AUTHENTICATION_KEY, authentication);
		// @Incomplete invalidate the session of the incoming request if it had one
		// @Hack
		if (response instanceof GrainHttpResponse res) {
			res.setCookie(gsid);
		}

		return authentication;
	}
}
