package com._7aske.grain.security.authentication;

import com._7aske.grain.component.Controller;
import com._7aske.grain.component.Inject;
import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.*;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.security.exception.GrainSecurityException;

/**
 * Default authentication entry point handling form POST requests to /login
 */
@Controller
@RequestMapping("/login")
public class FormLoginAuthenticationEntryPointController {
	@Inject
	private AuthenticationEntryPoint entryPoint;

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
	public String getLogin(HttpResponse response) {
		response.setHeader(HttpHeaders.CONTENT_TYPE, HttpContentType.TEXT_HTML);
		return "<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<title> Grain | Login </title>\n" +
				"<style>@import url(https://fonts.googleapis.com/css?family=Roboto:300);\n" +
				"header .header{\n" +
				"  background-color: #fff;\n" +
				"  height: 45px;\n" +
				"}\n" +
				"header a img{\n" +
				"  width: 134px;\n" +
				"margin-top: 4px;\n" +
				"}\n" +
				".login-page {\n" +
				"  width: 360px;\n" +
				"  padding: 8% 0 0;\n" +
				"  margin: auto;\n" +
				"}\n" +
				".login-page .form .login{\n" +
				"  margin-top: -31px;\n" +
				"margin-bottom: 26px;\n" +
				"}\n" +
				".form {\n" +
				"  position: relative;\n" +
				"  z-index: 1;\n" +
				"  background: #FFFFFF;\n" +
				"  max-width: 360px;\n" +
				"  margin: 0 auto 100px;\n" +
				"  padding: 45px;\n" +
				"  text-align: center;\n" +
				"  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);\n" +
				"}\n" +
				".form input {\n" +
				"  font-family: \"Roboto\", sans-serif;\n" +
				"  outline: 0;\n" +
				"  background: #f2f2f2;\n" +
				"  width: 100%;\n" +
				"  border: 0;\n" +
				"  margin: 0 0 15px;\n" +
				"  padding: 15px;\n" +
				"  box-sizing: border-box;\n" +
				"  font-size: 14px;\n" +
				"}\n" +
				".form button {\n" +
				"  font-family: \"Roboto\", sans-serif;\n" +
				"  text-transform: uppercase;\n" +
				"  outline: 0;\n" +
				"  background-color: #328f8a;\n" +
				"  background-image: linear-gradient(45deg,#328f8a,#08ac4b);\n" +
				"  width: 100%;\n" +
				"  border: 0;\n" +
				"  padding: 15px;\n" +
				"  color: #FFFFFF;\n" +
				"  font-size: 14px;\n" +
				"  -webkit-transition: all 0.3 ease;\n" +
				"  transition: all 0.3 ease;\n" +
				"  cursor: pointer;\n" +
				"}\n" +
				".container {\n" +
				"  position: relative;\n" +
				"  z-index: 1;\n" +
				"  max-width: 300px;\n" +
				"  margin: 0 auto;\n" +
				"}\n" +
				"\n" +
				"body {\n" +
				"  background-color: #328f8a;\n" +
				"  background-image: linear-gradient(45deg,#328f8a,#08ac4b);\n" +
				"  font-family: \"Roboto\", sans-serif;\n" +
				"  -webkit-font-smoothing: antialiased;\n" +
				"  -moz-osx-font-smoothing: grayscale;\n" +
				"}</style>" +
				"</head>\n" +
				"<body>\n" +
				"  <div class=\"login-page\">\n" +
				"    <div class=\"form\">\n" +
				"      <div class=\"login\">\n" +
				"        <div class=\"login-header\">\n" +
				"          <h3>LOGIN</h3>\n" +
				"          <p>Please enter your credentials to login.</p>\n" +
				"        </div>\n" +
				"      </div>\n" +
				"      <form method=\"post\" action=\"/login\" class=\"login-form\">\n" +
				"        <input name=\"username\" type=\"text\" placeholder=\"Username\"/>\n" +
				"        <input name=\"password\" type=\"password\" placeholder=\"Password\"/>\n" +
				"        <button>Login</button>\n" +
				"      </form>\n" +
				"    </div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>";
	}
}
